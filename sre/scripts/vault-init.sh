#!/usr/bin/env bash
# Seed the demo Vault with a couple of secrets and register the root
# token as a k8s Secret in the external-secrets namespace. ESO uses
# that Secret to authenticate against Vault.
#
# This script is the "chicken and egg" bootstrap. In prod you'd use
# Vault Kubernetes auth or AWS IAM auth instead of a static token,
# and the auth material would come from SealedSecrets or an IRSA role.
set -euo pipefail

VAULT_NS=vault
ESO_NS=external-secrets
VAULT_TOKEN=root

echo "waiting for vault pod to be Ready..."
kubectl -n "$VAULT_NS" wait --for=condition=Ready pod/vault-0 --timeout=5m

echo "seeding demo secrets into secret/backend..."
kubectl -n "$VAULT_NS" exec vault-0 -- sh -c "
  export VAULT_ADDR=http://127.0.0.1:8200
  export VAULT_TOKEN=$VAULT_TOKEN
  vault secrets enable -version=2 -path=secret kv 2>/dev/null || true
  vault kv put secret/backend \
    datasource_password='demo-db-password' \
    api_key='demo-api-key-$(date +%s)'
"

echo "creating ESO auth Secret in $ESO_NS..."
kubectl create namespace "$ESO_NS" --dry-run=client -o yaml | kubectl apply -f -
kubectl -n "$ESO_NS" create secret generic vault-token \
  --from-literal=token="$VAULT_TOKEN" \
  --dry-run=client -o yaml | kubectl apply -f -

echo "done. ExternalSecrets in any namespace can now reference ClusterSecretStore 'vault-backend'."
