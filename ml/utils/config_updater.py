import re


def update_thresholds_in_config(
    classifier_thresh: float, fallback_thresh: float, config_path="config.py"
):
    with open(config_path, "r") as f:
        content = f.read()

    # Replace classifier threshold
    content = re.sub(
        r'("confidence_threshold": )[\d.]+',
        lambda m: f"{m.group(1)}{classifier_thresh}",
        content,
    )

    # Replace fallback similarity threshold
    content = re.sub(
        r'("similarity_threshold": )[\d.]+',
        lambda m: f"{m.group(1)}{fallback_thresh}",
        content,
    )

    with open(config_path, "w") as f:
        f.write(content)

    print(
        f"Updated config.py with thresholds: classifier={classifier_thresh}, fallback={fallback_thresh}"
    )
