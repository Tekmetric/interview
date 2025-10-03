#!/usr/bin/env python3
import argparse
import base64
import copy
import difflib
import hashlib
import logging
import os
import signal
import subprocess
import sys
import tempfile
import threading
import time
from typing import Any, Dict, List, Optional, Set, Tuple

import yaml
from kubernetes import client, config, watch
from kubernetes.client.rest import ApiException
from rich.console import Console
from rich.logging import RichHandler
from rich.panel import Panel
from rich.syntax import Syntax
from rich.table import Table
from rich.text import Text

# --- Constants ---
FIND_DEPLOYMENT_TIMEOUT_SECONDS = 60


# --- Global State for Signal Handling ---
console = Console(
    force_terminal=os.getenv("CI")
)
log = logging.getLogger("rich")
HELM_PROCESS = None
SHUTDOWN_EVENT = threading.Event()


def graceful_shutdown(signum, frame):
    """Handles SIGINT and SIGTERM to shut down gracefully."""
    global HELM_PROCESS
    if SHUTDOWN_EVENT.is_set():
        return
    SHUTDOWN_EVENT.set()
    console.print(
        "\n[bold yellow]! Interrupt received, shutting down gracefully...[/bold yellow]")
    if HELM_PROCESS and HELM_PROCESS.poll() is None:
        log.info(f"Terminating Helm subprocess (PID: {HELM_PROCESS.pid})...")
        HELM_PROCESS.terminate()
        HELM_PROCESS.wait()
        log.info("Helm subprocess terminated.")
    sys.exit(130)

# --- HELPER FUNCTIONS ---


def get_all_key_paths(d: Dict, parent_path: Tuple = ()) -> Set[Tuple]:
    """Recursively gets all LEAF key paths from a dictionary (paths to actual values, not intermediate dicts)."""
    paths = set()
    for k, v in d.items():
        current_path = parent_path + (k,)
        if isinstance(v, dict):
            # Recurse into nested dictionaries
            paths.update(get_all_key_paths(v, current_path))
        else:
            # This is a leaf value - add its path
            paths.add(current_path)
    return paths


def _get_decrypted_secrets(secrets_file: str) -> Dict:
    """Uses helm-secrets to decrypt a file and returns its content as a dictionary."""
    try:
        result = subprocess.run(
            ["helm", "secrets", "decrypt", secrets_file],
            capture_output=True, text=True, check=False
        )
        if result.returncode != 0 and not result.stdout.strip():
            log.error(
                f"Failed to decrypt '{secrets_file}'. The command returned an error and no output.")
            log.error(f"Stderr: {result.stderr.strip()}")
            sys.exit(1)
        if not result.stdout.strip():
            log.warning(
                f"Decrypting '{secrets_file}' produced no output. Assuming it's empty.")
            return {}
        decrypted_values = yaml.safe_load(result.stdout) or {}
        decrypted_values.pop('sops', None)
        return decrypted_values
    except Exception as e:
        log.error(
            f"An unexpected Python error occurred during decryption of '{secrets_file}': {e}")
        sys.exit(1)


def _get_current_kubernetes_secrets(namespace: str, release_name: str) -> Dict:
    """Fetches and decodes all data from secrets managed by the Helm release."""
    current_secrets = {}
    try:
        v1 = client.CoreV1Api()
        secrets = v1.list_namespaced_secret(
            namespace=namespace,
            label_selector=f"app.kubernetes.io/instance={release_name}"
        )
        for secret in secrets.items:
            for key, value in (secret.data or {}).items():
                current_secrets[key] = base64.b64decode(value).decode('utf-8')
        return current_secrets
    except ApiException as e:
        log.error(f"Failed to fetch current secrets from Kubernetes API: {e}")
        log.error(
            "This is required for a secure diff. Please check your kubeconfig and network connection.")
        sys.exit(1)
    except Exception:
        log.error(
            "An unexpected error occurred while fetching secrets from Kubernetes.")
        log.error(
            "This is required for a secure diff. Please check your kubeconfig and network connection.")
        sys.exit(1)


def redact_values_by_path(data: Dict, secret_paths: Set[Tuple]) -> Dict:
    """
    Recursively finds secret values based on LEAF key paths and replaces them
    with a masked string showing a truncated SHA256 hash.
    """
    redacted_data = copy.deepcopy(data)

    def recursive_redact(d: Any, parent_path: Tuple = ()):
        if not isinstance(d, dict):
            return
        for key, value in d.items():
            current_path = parent_path + (key,)
            if current_path in secret_paths:
                # This is a secret leaf value - redact it
                if isinstance(value, str):
                    h = hashlib.sha256(value.encode()).hexdigest()
                    d[key] = f"**** (sha256:{h[:8]}...)"
                elif value is not None:
                    h = hashlib.sha256(str(value).encode()).hexdigest()
                    d[key] = f"**** (sha256:{h[:8]}...)"
            elif isinstance(value, dict):
                recursive_redact(value, current_path)
    recursive_redact(redacted_data)
    return redacted_data


def filter_dict_by_keys(data_to_filter: Dict, keys_to_keep: Dict) -> Dict:
    """Recursively filters a dictionary, keeping only the keys that exist in a reference dictionary."""
    if not isinstance(data_to_filter, dict) or not isinstance(keys_to_keep, dict):
        return data_to_filter
    filtered = {}
    for key, value in keys_to_keep.items():
        if key in data_to_filter:
            if isinstance(value, dict):
                filtered[key] = filter_dict_by_keys(
                    data_to_filter.get(key, {}), value)
            else:
                filtered[key] = data_to_filter[key]
    return filtered


def get_current_helm_values(release_name: str, namespace: str) -> Dict:
    """Fetches the current deployed values for a Helm release."""
    log.info(f"Fetching current values for release '{release_name}'...")
    command = ["helm", "get", "values", release_name,
               "--namespace", namespace, "-o", "yaml"]
    try:
        result = subprocess.run(
            command, capture_output=True, text=True, check=False)
        if result.returncode != 0:
            if "not found" in result.stderr:
                log.warning(
                    f"Release '{release_name}' not found. Assuming this is a new installation.")
                return {}
            log.error(f"Failed to get current Helm values: {result.stderr}")
            return {}
        return yaml.safe_load(result.stdout) or {}
    except FileNotFoundError:
        log.error("'helm' command not found. Cannot fetch current values.")
        return {}
    except Exception as e:
        log.error(f"An error occurred while fetching current values: {e}")
        return {}


def display_diff_and_values(
    current_values: Dict,
    values_file: str,
    secrets_file: str
):
    """Displays the user-supplied values and a diff with hashed secrets."""
    # Load base values without secrets
    base_values = {}
    if os.path.exists(values_file):
        try:
            with open(values_file, 'r') as f:
                base_values = yaml.safe_load(f) or {}
        except Exception as e:
            log.error(f"Failed to load values file {values_file}: {e}")
            return

    # Decrypt secrets to get their paths
    decrypted_secrets = _get_decrypted_secrets(secrets_file)
    secret_paths = get_all_key_paths(decrypted_secrets)

    # Merge base values with decrypted secrets for the complete user values
    user_supplied_values = copy.deepcopy(base_values)
    deep_merge(user_supplied_values, decrypted_secrets)

    # 1. Display the user-supplied values with secrets masked
    masked_user_values = redact_values_by_path(
        user_supplied_values, secret_paths)
    user_values_yaml = yaml.dump(
        masked_user_values, indent=2, default_flow_style=False)

    code_width = None
    if not sys.stdout.isatty or console._force_terminal:
        code_width = 120
    syntax = Syntax(user_values_yaml, "yaml", code_width=code_width,
                    word_wrap=True, theme="monokai", line_numbers=True)

    console.print(Panel(
        syntax,  title="[bold yellow]📝 User-Supplied Values (Secrets Masked)[/bold yellow]", border_style="yellow"))

    # 2. Prepare the diff
    shadow_new_values = redact_values_by_path(
        user_supplied_values, secret_paths)
    shadow_current_values = redact_values_by_path(current_values, secret_paths)
    current_filtered = filter_dict_by_keys(
        shadow_current_values, user_supplied_values)

    # 3. Generate and display the diff
    current_yaml = yaml.dump(current_filtered, indent=2,
                             default_flow_style=False).splitlines()
    new_yaml = yaml.dump(shadow_new_values, indent=2,
                         default_flow_style=False).splitlines()
    diff = list(difflib.unified_diff(current_yaml, new_yaml,
                fromfile="current", tofile="new", lineterm=''))

    if not diff:
        log.info("[green]✔[/green] No changes detected in user-supplied values.")
        return

    diff_text = Text()
    for line in diff:
        if line.startswith('+'):
            diff_text.append(line + '\n', style="green")
        elif line.startswith('-'):
            diff_text.append(line + '\n', style="red")
        else:
            diff_text.append(line + '\n')

    console.print(Panel(
        diff_text, title="[bold yellow]🔄 Diff of User-Supplied Values (Secrets Hashed)[/bold yellow]", border_style="yellow"))


def get_user_supplied_values(values_file: str, secrets_file: str) -> Dict:
    """
    Loads, decrypts, and merges user-provided values and secrets files
    into a single dictionary of plaintext values.
    """
    log.info("Loading and decrypting user-supplied values...")
    user_values = {}

    if os.path.exists(values_file):
        try:
            with open(values_file, 'r') as f:
                base_values = yaml.safe_load(f) or {}
                deep_merge(user_values, base_values)
        except Exception as e:
            log.error(f"Failed to load values file {values_file}: {e}")
            return {}

    if os.path.exists(secrets_file):
        decrypted_secrets = _get_decrypted_secrets(secrets_file)
        deep_merge(user_values, decrypted_secrets)

    return user_values


def extract_chart_values(chart_path: str, values_file: str, secrets_file: str) -> Dict:
    """Extracts and merges values from the chart defaults and user-supplied files."""
    log.info("Merging final values (chart defaults + user values)...")
    merged_values = {}
    try:
        with tempfile.TemporaryDirectory() as tmpdir:
            subprocess.run(["tar", "-xzf", chart_path, "-C",
                           tmpdir], check=True, capture_output=True)
            for root, _, files in os.walk(tmpdir):
                if "values.yaml" in files:
                    values_path = os.path.join(root, "values.yaml")
                    with open(values_path, 'r') as f:
                        merged_values = yaml.safe_load(f) or {}
                    break

        user_values = get_user_supplied_values(values_file, secrets_file)
        deep_merge(merged_values, user_values)

        return merged_values
    except Exception as e:
        log.warning(f"Could not extract final merged values: {e}")
        return {}


def deep_merge(base: Dict, override: Dict) -> None:
    """Recursively merges override dict into base dict."""
    for key, value in override.items():
        if key in base and isinstance(base[key], dict) and isinstance(value, dict):
            deep_merge(base[key], value)
        else:
            base[key] = value


def parse_arguments():
    parser = argparse.ArgumentParser(
        description="Helm Deployment Monitor Script")
    parser.add_argument("--namespace", required=True,
                        help="Kubernetes namespace for the deployment")
    parser.add_argument("--release-name", required=True,
                        help="Helm release name")
    parser.add_argument("--chart-path", required=True,
                        help="Path to the Helm chart tgz file")
    parser.add_argument("--values-file", required=True,
                        help="Path to the primary Helm values file")
    parser.add_argument("--secrets-file", required=True,
                        help="Path to the Helm secrets file")
    parser.add_argument("--timeout", type=int, default=0,
                        help="Override timeout in seconds (0=auto-calculate)")
    parser.add_argument("--safety-factor", type=float, default=2.0,
                        help="Safety factor for timeout calculation")
    parser.add_argument("--dry-run", action="store_true",
                        help="Run in dry-run mode (validation only)")
    parser.add_argument("--kube-context", help="The kubeconfig context to use")
    return parser.parse_args()


def calculate_deployment_timeout(values: Dict, safety_factor: float) -> int:
    log.info("Calculating deployment timeout...")
    startup_probe = values.get('startupProbe', {})
    readiness_probe = values.get('readinessProbe', {})
    replica_count = values.get('replicaCount', 3)

    # Get deployment strategy settings
    strategy = values.get('strategy', {})
    max_surge = strategy.get('rollingUpdate', {}).get('maxSurge', '25%')
    max_unavailable = strategy.get(
        'rollingUpdate', {}).get('maxUnavailable', '25%')

    # Calculate how many pods can be updated in parallel
    if isinstance(max_surge, str) and max_surge.endswith('%'):
        surge_count = max(1, int(replica_count * int(max_surge[:-1]) / 100))
    else:
        surge_count = int(max_surge) if max_surge else 1

    if isinstance(max_unavailable, str) and max_unavailable.endswith('%'):
        unavailable_count = max(
            1, int(replica_count * int(max_unavailable[:-1]) / 100))
    else:
        unavailable_count = int(max_unavailable) if max_unavailable else 1

    # Parallel update capacity
    parallel_updates = surge_count + unavailable_count

    startup_time = 0
    if startup_probe.get('enabled', True):
        spec = startup_probe.get('spec', {})
        failure_threshold = spec.get('failureThreshold', 30)
        period_seconds = spec.get('periodSeconds', 5)
        initial_delay = spec.get('initialDelaySeconds', 0)
        startup_time = initial_delay + (failure_threshold * period_seconds)

    readiness_time = 0
    if readiness_probe.get('enabled', True):
        spec = readiness_probe.get('spec', {})
        failure_threshold = spec.get('failureThreshold', 3)
        period_seconds = spec.get('periodSeconds', 10)
        initial_delay = spec.get('initialDelaySeconds', 15)
        readiness_time = initial_delay + (failure_threshold * period_seconds)

    # Time for one pod to become ready
    pod_ready_time = startup_time + readiness_time

    # Calculate rolling update waves needed
    update_waves = max(1, replica_count // parallel_updates)

    # Base timeout: waves * time per wave + buffer
    base_timeout = max(update_waves * pod_ready_time, 60)

    # Apply safety factor
    calculated_timeout = int(base_timeout * safety_factor)

    # Set reasonable bounds based on replica count
    if replica_count <= 10:
        min_timeout = 300  # 5 minutes minimum for small deployments
        max_timeout = 1800  # 30 minutes max for small deployments
    elif replica_count <= 100:
        min_timeout = 600  # 10 minutes minimum
        max_timeout = 7200  # 2 hours max
    else:
        min_timeout = 1800  # 30 minutes minimum for large deployments
        max_timeout = 14400  # 4 hours max for very large deployments

    final_timeout = max(min_timeout, min(calculated_timeout, max_timeout))

    table = Table(title="⏱️ Deployment Timeout Calculation",
                  show_header=True, header_style="bold magenta")
    table.add_column("Parameter", style="dim")
    table.add_column("Value", style="bold")
    table.add_row("Startup Probe Time", f"{startup_time}s")
    table.add_row("Readiness Probe Time", f"{readiness_time}s")
    table.add_row("Time per Pod", f"{pod_ready_time}s")
    table.add_row("Replica Count", str(replica_count))
    table.add_row("Parallel Updates", str(parallel_updates))
    table.add_row("Update Waves", str(update_waves))
    table.add_row("Safety Factor", str(safety_factor))
    table.add_row("Calculated Timeout",
                  f"{calculated_timeout}s ({calculated_timeout//60}m)")
    table.add_row(
        "Final Timeout", f"{final_timeout}s ({final_timeout//60}m)", style="bold green")
    console.print(table)

    if calculated_timeout > max_timeout:
        log.warning(
            f"[yellow]⚠[/yellow] Calculated timeout ({calculated_timeout}s) exceeds maximum ({max_timeout}s). Using maximum.")
        log.warning("Consider using --timeout flag for very large deployments.")

    return final_timeout


def validate_cluster_resources(namespace: str, values: Dict) -> Tuple[bool, str]:
    """Validates that the cluster has sufficient resources for the deployment."""
    log.info("Validating cluster resources...")
    try:
        v1 = client.CoreV1Api()

        # Calculate required resources
        replica_count = values.get('replicaCount', 3)
        size_profile = values.get('sizeProfile', 'medium')
        resources = values.get('resources', {})
        if size_profile != 'custom' and 'sizing' in values:
            resources = values.get('sizing', {}).get(
                'profiles', {}).get(size_profile, {})

        requests = resources.get('requests', {})
        limits = resources.get('limits', {})
        cpu_req_per_pod = parse_cpu(requests.get('cpu', '100m'))
        mem_req_per_pod = parse_memory(requests.get('memory', '128Mi'))
        cpu_limit_per_pod = parse_cpu(limits.get('cpu', '0'))
        mem_limit_per_pod = parse_memory(limits.get('memory', '0'))
        total_cpu_req = cpu_req_per_pod * replica_count
        total_mem_req = mem_req_per_pod * replica_count
        total_cpu_limit = cpu_limit_per_pod * replica_count if cpu_limit_per_pod else 0
        total_mem_limit = mem_limit_per_pod * replica_count if mem_limit_per_pod else 0

        # Check namespace-level constraints first
        namespace_issues = []

        # Check ResourceQuotas
        quotas = v1.list_namespaced_resource_quota(namespace=namespace)
        for quota in quotas.items:
            if quota.status and quota.status.hard:
                hard = quota.status.hard
                used = quota.status.used or {}

                # Check CPU requests quota
                if 'requests.cpu' in hard:
                    quota_cpu = parse_cpu(hard['requests.cpu'])
                    used_cpu = parse_cpu(used.get('requests.cpu', '0'))
                    available_cpu = quota_cpu - used_cpu
                    if total_cpu_req > available_cpu:
                        namespace_issues.append(
                            f"CPU request quota: need {format_cpu(total_cpu_req)}, "
                            f"but only {format_cpu(available_cpu)} available in quota '{quota.metadata.name}'"
                        )

                # Check memory requests quota
                if 'requests.memory' in hard:
                    quota_mem = parse_memory(hard['requests.memory'])
                    used_mem = parse_memory(used.get('requests.memory', '0'))
                    available_mem = quota_mem - used_mem
                    if total_mem_req > available_mem:
                        namespace_issues.append(
                            f"Memory request quota: need {format_memory(total_mem_req)}, "
                            f"but only {format_memory(available_mem)} available in quota '{quota.metadata.name}'"
                        )

                # Check CPU limits quota
                if 'limits.cpu' in hard and total_cpu_limit > 0:
                    quota_cpu = parse_cpu(hard['limits.cpu'])
                    used_cpu = parse_cpu(used.get('limits.cpu', '0'))
                    available_cpu = quota_cpu - used_cpu
                    if total_cpu_limit > available_cpu:
                        namespace_issues.append(
                            f"CPU limit quota: need {format_cpu(total_cpu_limit)}, "
                            f"but only {format_cpu(available_cpu)} available in quota '{quota.metadata.name}'"
                        )

                # Check memory limits quota
                if 'limits.memory' in hard and total_mem_limit > 0:
                    quota_mem = parse_memory(hard['limits.memory'])
                    used_mem = parse_memory(used.get('limits.memory', '0'))
                    available_mem = quota_mem - used_mem
                    if total_mem_limit > available_mem:
                        namespace_issues.append(
                            f"Memory limit quota: need {format_memory(total_mem_limit)}, "
                            f"but only {format_memory(available_mem)} available in quota '{quota.metadata.name}'"
                        )

                # Check pod count quota
                if 'count/pods' in hard or 'pods' in hard:
                    quota_pods = int(
                        hard.get('count/pods', hard.get('pods', '0')))
                    used_pods = int(
                        used.get('count/pods', used.get('pods', '0')))
                    available_pods = quota_pods - used_pods
                    if replica_count > available_pods:
                        namespace_issues.append(
                            f"Pod count quota: need {replica_count} pods, "
                            f"but only {available_pods} available in quota '{quota.metadata.name}'"
                        )

        # Check LimitRanges
        limit_ranges = v1.list_namespaced_limit_range(namespace=namespace)
        for lr in limit_ranges.items:
            for limit in lr.spec.limits:
                if limit.type == 'Pod' or limit.type == 'Container':
                    # Check min constraints
                    if limit.min:
                        if 'cpu' in limit.min and cpu_req_per_pod < parse_cpu(limit.min['cpu']):
                            namespace_issues.append(
                                f"CPU request below minimum in LimitRange '{lr.metadata.name}': "
                                f"{format_cpu(cpu_req_per_pod)} < {limit.min['cpu']}"
                            )
                        if 'memory' in limit.min and mem_req_per_pod < parse_memory(limit.min['memory']):
                            namespace_issues.append(
                                f"Memory request below minimum in LimitRange '{lr.metadata.name}': "
                                f"{format_memory(mem_req_per_pod)} < {limit.min['memory']}"
                            )

                    # Check max constraints
                    if limit.max:
                        check_cpu = cpu_limit_per_pod if cpu_limit_per_pod else cpu_req_per_pod
                        check_mem = mem_limit_per_pod if mem_limit_per_pod else mem_req_per_pod

                        if 'cpu' in limit.max and check_cpu > parse_cpu(limit.max['cpu']):
                            namespace_issues.append(
                                f"CPU exceeds maximum in LimitRange '{lr.metadata.name}': "
                                f"{format_cpu(check_cpu)} > {limit.max['cpu']}"
                            )
                        if 'memory' in limit.max and check_mem > parse_memory(limit.max['memory']):
                            namespace_issues.append(
                                f"Memory exceeds maximum in LimitRange '{lr.metadata.name}': "
                                f"{format_memory(check_mem)} > {limit.max['memory']}"
                            )

        # If namespace constraints fail, show them and stop
        if namespace_issues:
            log.error(
                f"[red]✗[/red] Namespace '{namespace}' constraints validation failed:")
            for issue in namespace_issues:
                log.error(f"  • {issue}")
            return False, f"Namespace constraints violated: {len(namespace_issues)} issue(s)"

        # Now check cluster-wide resources
        nodes = v1.list_node()
        total_allocatable_cpu = 0
        total_allocatable_mem = 0
        total_available_cpu = 0
        total_available_mem = 0

        for node in nodes.items:
            if node.spec.unschedulable:
                continue

            has_noschedule = False
            if node.spec.taints:
                for taint in node.spec.taints:
                    if taint.effect == "NoSchedule" and not taint.key.startswith("node.kubernetes.io/"):
                        has_noschedule = True
                        break
            if has_noschedule:
                continue

            allocatable = node.status.allocatable
            node_cpu = parse_cpu(allocatable.get('cpu', '0'))
            node_mem = parse_memory(allocatable.get('memory', '0'))
            total_allocatable_cpu += node_cpu
            total_allocatable_mem += node_mem

            pods = v1.list_pod_for_all_namespaces(
                field_selector=f"spec.nodeName={node.metadata.name}")
            used_cpu = 0
            used_mem = 0

            for pod in pods.items:
                if pod.status.phase in ['Succeeded', 'Failed']:
                    continue

                for container in (pod.spec.containers or []):
                    if container.resources and container.resources.requests:
                        used_cpu += parse_cpu(
                            container.resources.requests.get('cpu', '0'))
                        used_mem += parse_memory(
                            container.resources.requests.get('memory', '0'))

            available_cpu = node_cpu - used_cpu
            available_mem = node_mem - used_mem
            total_available_cpu += max(0, available_cpu)
            total_available_mem += max(0, available_mem)

        cpu_sufficient = total_available_cpu >= total_cpu_req
        mem_sufficient = total_available_mem >= total_mem_req

        # Display results table
        table = Table(title="🔎 Resource Validation",
                      show_header=True, header_style="bold blue")
        table.add_column("Resource", style="dim")
        table.add_column("Required", style="bold")
        table.add_column("Available", style="bold")
        table.add_column("Status", style="bold")

        cpu_status = "[green]✓[/green]" if cpu_sufficient else "[red]✗[/red]"
        mem_status = "[green]✓[/green]" if mem_sufficient else "[red]✗[/red]"

        table.add_row(
            f"CPU ({replica_count} replicas)",
            format_cpu(total_cpu_req),
            format_cpu(total_available_cpu),
            cpu_status
        )
        table.add_row(
            f"Memory ({replica_count} replicas)",
            format_memory(total_mem_req),
            format_memory(total_available_mem),
            mem_status
        )

        if quotas.items:
            table.add_row("", "", "", "")
            table.add_row(f"Namespace Quotas", "✓ Passed",
                          "", "[green]✓[/green]")
        if limit_ranges.items:
            table.add_row(f"LimitRanges", "✓ Passed", "", "[green]✓[/green]")

        console.print(table)

        if cpu_sufficient and mem_sufficient:
            log.info("[green]✔[/green] Resource validation passed.")
            return True, "Sufficient resources available"
        else:
            issues = []
            if not cpu_sufficient:
                deficit = total_cpu_req - total_available_cpu
                issues.append(f"CPU deficit: {format_cpu(deficit)}")
            if not mem_sufficient:
                deficit = total_mem_req - total_available_mem
                issues.append(f"Memory deficit: {format_memory(deficit)}")

            log.warning(
                f"[yellow]⚠[/yellow] Insufficient cluster resources: {', '.join(issues)}")
            log.warning("Deployment may fail or cause pod evictions.")

            return True, f"Proceeding despite: {', '.join(issues)}"

    except ApiException as e:
        log.warning(f"Could not validate resources via Kubernetes API: {e}")
        log.info("Proceeding without validation.")
        return True, "Validation skipped - API error"
    except Exception as e:
        log.warning(f"Could not validate resources: {e}")
        log.info("Proceeding without validation.")
        return True, "Validation skipped due to error"


def format_cpu(cpu_millicores):
    """Format CPU display as cores or millicores"""
    if cpu_millicores >= 1000:
        return f"{cpu_millicores / 1000:.1f} cores"
    else:
        return f"{cpu_millicores:.0f}m"


def format_memory(mem_bytes):
    """Format memory display as GiB, MiB based on size"""
    gib = mem_bytes / (1024**3)
    mib = mem_bytes / (1024**2)

    if gib >= 1:
        return f"{gib:.1f} GiB"
    else:
        return f"{mib:.0f} MiB"


def parse_cpu(cpu_str: str) -> float:
    if not cpu_str:
        return 0
    cpu_str = str(cpu_str).strip()
    return float(cpu_str[:-1]) if cpu_str.endswith('m') else float(cpu_str) * 1000


def parse_memory(mem_str: str) -> float:
    if not mem_str:
        return 0
    mem_str = str(mem_str).strip()
    units = {'Ki': 1024, 'Mi': 1024**2, 'Gi': 1024 **
             3, 'K': 1000, 'M': 1000**2, 'G': 1000**3}
    for unit, multiplier in units.items():
        if mem_str.endswith(unit):
            return float(mem_str[:-len(unit)]) * multiplier
    return float(mem_str)


def _get_secret_values_to_redact(secrets_file: str) -> List[str]:
    """Loads a decrypted secrets file and returns a list of its string values for redaction in dry-run output."""
    secrets = []
    decrypted_values = _get_decrypted_secrets(secrets_file)

    def find_strings(data):
        if isinstance(data, dict):
            for v in data.values():
                find_strings(v)
        elif isinstance(data, list):
            for item in data:
                find_strings(item)
        elif isinstance(data, str) and data:
            secrets.append(data)
    find_strings(decrypted_values)
    return secrets


def run_helm_upgrade_threaded(
    namespace: str, release_name: str, chart_path: str,
    values_file: str, secrets_file: str, timeout: int, dry_run: bool,
    result_dict: Dict, helm_done_event: threading.Event
):
    """Executes a helm upgrade command in a thread and streams its output with redaction."""
    global HELM_PROCESS
    log.info(
        f"Starting Helm {'dry-run' if dry_run else 'upgrade'} for release '{release_name}'...")
    command = [
        "helm", "secrets", "upgrade", "--install",
        release_name, chart_path,
        "--namespace", namespace,
        "--values", values_file,
        "--values", secrets_file,
        "--timeout", f"{timeout}s",
        "--create-namespace",
    ]
    if dry_run:
        command.extend(["--wait", "--dry-run"])
    else:
        command.append("--wait")

    try:
        secrets_to_redact = []
        if dry_run:
            secrets_to_redact = _get_secret_values_to_redact(secrets_file)
            log.info(
                f"Loaded {len(secrets_to_redact)} secret values for dry-run redaction.")
        process = subprocess.Popen(
            command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            text=True, encoding='utf-8', errors='replace'
        )
        HELM_PROCESS = process
        for line in iter(process.stdout.readline, ''):
            if line:
                output_line = line.strip()
                if dry_run and secrets_to_redact:
                    for secret in secrets_to_redact:
                        encoded_secret = str(base64.b64encode(
                            secret.encode('utf-8')), "utf-8")
                        if secret in output_line:
                            output_line = output_line.replace(
                                secret, "[REDACTED]")
                        if encoded_secret in output_line:
                            output_line = output_line.replace(
                                encoded_secret, "[REDACTED_BASE64]")
                log.info(f"[bold blue][Helm][/bold blue] {output_line}")
        process.stdout.close()
        return_code = process.wait()
        result_dict['returncode'] = return_code
        if return_code != 0:
            log.error(f"Helm command failed with exit code {return_code}.")
        else:
            log.info("Helm command finished successfully.")
    except FileNotFoundError:
        log.error("'helm' command not found. Is Helm installed and in your PATH?")
        result_dict['returncode'] = -1
    except Exception as e:
        log.error(f"An exception occurred while running Helm: {e}")
        result_dict['returncode'] = -1
    finally:
        helm_done_event.set()


def get_deployment_name(namespace: str, release_name: str) -> Optional[str]:
    try:
        api = client.AppsV1Api()
        deployments = api.list_namespaced_deployment(
            namespace=namespace, label_selector=f"app.kubernetes.io/instance={release_name}"
        )
        return deployments.items[0].metadata.name if deployments.items else None
    except Exception as e:
        log.warning(
            f"[bold magenta][Monitor][/bold magenta] Could not determine deployment name: {e}")
        return None


def get_current_deployment_generation(apps_v1: client.AppsV1Api, namespace: str, release_name: str) -> int:
    """Gets the generation of the currently deployed release, returning 0 if not found."""
    deployment_name = get_deployment_name(namespace, release_name)
    if not deployment_name:
        return 0
    try:
        deployment = apps_v1.read_namespaced_deployment(
            deployment_name, namespace)
        return deployment.metadata.generation
    except ApiException:
        log.warning(
            f"[bold magenta][Monitor][/bold magenta] Could not read deployment '{deployment_name}'. Assuming generation 0.")
        return 0


def monitor_deployment_rollout(namespace: str, release_name: str, timeout_seconds: int, initial_generation: int, result_dict: Dict, helm_done_event: threading.Event, helm_result: Dict):
    """Monitors a deployment's rollout, first waiting for its generation to increase."""
    is_ci = os.getenv("CI") == "true" or os.getenv("GITHUB_ACTIONS") == "true"

    def perform_monitoring(status_updater=None):
        try:
            apps_v1 = client.AppsV1Api()
            deployment_name = None
            find_deadline = time.time() + FIND_DEPLOYMENT_TIMEOUT_SECONDS
            target_generation = -1
            log.info(
                f"[bold magenta][Monitor][/bold magenta] Waiting for deployment generation to update from {initial_generation}...")
            while time.time() < find_deadline:
                if helm_done_event.is_set():
                    break
                if SHUTDOWN_EVENT.is_set():
                    return
                deployment_name = get_deployment_name(namespace, release_name)
                if deployment_name:
                    try:
                        deployment = apps_v1.read_namespaced_deployment(
                            deployment_name, namespace)
                        if deployment.metadata.generation > initial_generation:
                            target_generation = deployment.metadata.generation
                            log.info(
                                f"[bold magenta][Monitor][/bold magenta] New deployment generation {target_generation} found. Starting status watch.")
                            break
                    except ApiException:
                        pass
                if status_updater:
                    status_updater(
                        f"Waiting for new deployment version to be applied...")
                time.sleep(2)
            if helm_done_event.is_set():
                helm_success = helm_result.get('returncode') == 0
                if not helm_success:
                    log.error(
                        "[bold magenta][Monitor][/bold magenta] Helm command failed. Aborting monitoring.")
                    result_dict['success'] = False
                    return
                if target_generation == -1:
                    log.info(
                        "[bold magenta][Monitor][/bold magenta] Helm finished with no new deployment generation. Assuming no changes were applied.")
                    result_dict['success'] = True
                    return
            if not deployment_name or target_generation == -1:
                log.error(
                    f"[bold magenta][Monitor][/bold magenta] Did not detect a new deployment version within {FIND_DEPLOYMENT_TIMEOUT_SECONDS}s.")
                result_dict['success'] = False
                return

            start_time = time.time()
            w = watch.Watch()
            stream_timeout = timeout_seconds - (time.time() - start_time)
            if stream_timeout <= 0:
                log.error(
                    "[bold magenta][Monitor][/bold magenta] Ran out of time before monitoring could start.")
                result_dict['success'] = False
                return

            stream = w.stream(
                apps_v1.list_namespaced_deployment, namespace=namespace,
                field_selector=f"metadata.name={deployment_name}",
                timeout_seconds=int(stream_timeout)
            )
            last_status = None
            for event in stream:
                if SHUTDOWN_EVENT.is_set():
                    return
                deployment = event.get('object')
                if not isinstance(deployment, client.V1Deployment):
                    continue
                if deployment.status.observed_generation < target_generation:
                    continue
                replicas = deployment.spec.replicas or 1
                updated = deployment.status.updated_replicas or 0
                available = deployment.status.available_replicas or 0
                current_status = (updated, available)
                status_text = (
                    f"Monitoring status... [bold yellow]Updated: {updated}/{replicas}[/bold yellow] | "
                    f"[bold green]Available: {available}/{replicas}[/bold green]"
                )
                if status_updater:
                    status_updater(status_text)
                elif current_status != last_status:
                    log.info(
                        f"[bold magenta][Monitor][/bold magenta] [{deployment_name}] Status - Updated: {updated}/{replicas}, Available: {available}/{replicas}")
                last_status = current_status
                if (updated >= replicas and available >= replicas):
                    log.info(
                        f"[bold magenta][Monitor][/bold magenta] Post-flight check PASSED: Deployment generation {target_generation} is fully rolled out.")
                    result_dict['success'] = True
                    return
            log.error(
                f"[bold magenta][Monitor][/bold magenta] Watch timed out for '{deployment_name}'.")
            capture_deployment_debug_info(namespace, release_name)
            result_dict['success'] = False
        except Exception as e:
            if not SHUTDOWN_EVENT.is_set():
                log.error(
                    f"[bold magenta][Monitor][/bold magenta] Unexpected error: {e}", exc_info=True)
            result_dict['success'] = False

    if not is_ci:
        with console.status("", spinner="dots") as status:
            perform_monitoring(status_updater=status.update)
    else:
        log.info(
            "[bold magenta][Monitor][/bold magenta] CI environment detected. Using simple line-by-line logging.")
        perform_monitoring()


def capture_deployment_debug_info(namespace: str, release_name: str):
    log.warning(
        "[bold magenta][Monitor][/bold magenta] Capturing debug information for failed release...")
    try:
        v1 = client.CoreV1Api()
        pods = v1.list_namespaced_pod(
            namespace=namespace, label_selector=f"app.kubernetes.io/instance={release_name}")
        if not pods.items:
            log.info(
                "[bold magenta][Monitor][/bold magenta] No pods found for this release to debug.")
            return
        for pod in pods.items[:3]:
            log.info(
                f"[bold magenta][Monitor][/bold magenta] --- Pod: {pod.metadata.name} | Status: {pod.status.phase} ---")
            for status in (pod.status.container_statuses or []):
                log.info(
                    f"[bold magenta][Monitor][/bold magenta]   Container: {status.name}, Ready: {status.ready}, Restarts: {status.restart_count}")
                if status.state.waiting:
                    log.warning(
                        f"[bold magenta][Monitor][/bold magenta]     State: Waiting - {status.state.waiting.reason}: {status.state.waiting.message}")
                if status.state.terminated:
                    log.warning(
                        f"[bold magenta][Monitor][/bold magenta]     State: Terminated - {status.state.terminated.reason}, Exit Code: {status.state.terminated.exit_code}")
    except Exception as e:
        log.error(
            f"[bold magenta][Monitor][/bold magenta] Could not capture debug info: {e}")


def main():
    """Main execution function."""
    signal.signal(signal.SIGINT, graceful_shutdown)
    signal.signal(signal.SIGTERM, graceful_shutdown)

    logging.basicConfig(
        level="INFO", format="%(message)s", datefmt="[%X]",
        handlers=[RichHandler(
            console=console, rich_tracebacks=True, show_path=False, markup=True)]
    )
    args = parse_arguments()
    console.rule(
        f"[bold blue]🚀 Starting Helm Deployment: {args.release_name}", style="blue")

    config.load_kube_config(context=args.kube_context)

    current_values = get_current_helm_values(args.release_name, args.namespace)
    display_diff_and_values(
        current_values, args.values_file, args.secrets_file)

    final_merged_values = extract_chart_values(
        args.chart_path, args.values_file, args.secrets_file)
    if not final_merged_values:
        sys.exit(1)

    timeout = args.timeout if args.timeout > 0 else calculate_deployment_timeout(
        final_merged_values, args.safety_factor)

    validation_passed, validation_message = validate_cluster_resources(
        args.namespace, final_merged_values)

    if validation_passed:
        if "Sufficient resources" in validation_message:
            console.print(Panel(
                Text(validation_message, justify="center"),
                title="[bold green]✅ Resource Validation Passed[/bold green]",
                border_style="green"
            ))
        elif "Proceeding despite" in validation_message:
            console.print(Panel(
                Text(validation_message, justify="center"),
                title="[bold yellow]⚠️ Resource Validation Warning[/bold yellow]",
                border_style="yellow"
            ))
        else:
            console.print(Panel(
                Text(validation_message, justify="center"),
                title="[bold blue]ℹ️ Resource Validation[/bold blue]",
                border_style="blue"
            ))
    else:
        console.print(Panel(
            Text(validation_message, justify="center"),
            title="[bold red]❌ Resource Validation Failed[/bold red]",
            border_style="red"
        ))
        sys.exit(1)

    if args.dry_run:
        helm_result = {}
        run_helm_upgrade_threaded(
            args.namespace, args.release_name, args.chart_path,
            args.values_file, args.secrets_file, timeout, True, helm_result, threading.Event()
        )
        if helm_result.get('returncode') == 0:
            console.print(Panel(Text("Dry-run completed successfully.", justify="center"),
                          title="[bold green]✅ Dry-Run Result[/bold green]", border_style="green"))
            sys.exit(0)
        else:
            console.print(Panel(Text("Dry-run failed.", justify="center"),
                          title="[bold red]❌ Dry-Run Result[/bold red]", border_style="red"))
            sys.exit(1)

    apps_v1 = client.AppsV1Api()
    initial_generation = get_current_deployment_generation(
        apps_v1, args.namespace, args.release_name)
    monitor_result = {}
    helm_result = {}
    helm_done_event = threading.Event()
    monitor_thread = threading.Thread(
        target=monitor_deployment_rollout,
        args=(args.namespace, args.release_name, timeout,
              initial_generation, monitor_result, helm_done_event, helm_result),
        daemon=True
    )
    helm_thread = threading.Thread(
        target=run_helm_upgrade_threaded,
        args=(
            args.namespace, args.release_name, args.chart_path,
            args.values_file, args.secrets_file, timeout, False, helm_result, helm_done_event
        ),
    )

    log.info("Starting background monitor and Helm upgrade...")
    monitor_thread.start()
    helm_thread.start()
    helm_thread.join(timeout=timeout + 60)
    monitor_thread.join(timeout=60)

    if helm_thread.is_alive():
        log.error("Helm upgrade command timed out.")
        graceful_shutdown(None, None)

    helm_success = helm_result.get('returncode') == 0
    monitor_had_no_rollout = 'success' not in monitor_result
    monitor_success = monitor_result.get('success', False)

    final_success = helm_success and (
        monitor_success or monitor_had_no_rollout)

    if final_success:
        console.print(Panel(
            Text(
                f"Release '{args.release_name}' deployed successfully to namespace '{args.namespace}'.", justify="center"),
            title="[bold green]✅ Deployment Succeeded[/bold green]",
            border_style="green"
        ))
        sys.exit(0)
    else:
        reasons = []
        if not helm_success:
            reasons.append(
                f"Helm command failed with code {helm_result.get('returncode')}.")
        elif not monitor_success:
            reasons.append("Post-flight monitoring check failed or timed out.")

        console.print(Panel(
            Text("\n".join(
                reasons) if reasons else "Deployment failed for an unknown reason.", justify="center"),
            title="[bold red]❌ Deployment Failed[/bold red]",
            border_style="red"
        ))
        sys.exit(1)


if __name__ == "__main__":
    main()
