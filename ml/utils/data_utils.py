def filter_dataset_for_training(df, label_col, min_count=2):
    """
    Filters out rows in the dataset where the label occurs fewer than `min_count` times.
    This supports stratified train-test splitting for imbalanced data sets.

    Args:
        df (pd.DataFrame): Input dataset with a label column.
        label_col (str): Name of the column containing the label.
        min_count (int): Minimum number of times a label must appear to be kept.

    Returns:
        pd.DataFrame: Filtered dataset.
        List[str]: List of labels that were removed.
    """
    label_counts = df[label_col].value_counts()
    rare_labels = label_counts[label_counts < min_count]

    if not rare_labels.empty:
        print(
            "\nRemoving the following rare labels with fewer than",
            min_count,
            "examples:",
        )
        for label, count in rare_labels.items():
            print(f"  - {label}: {count} occurrence{'s' if count > 1 else ''}")
    else:
        print("All labels have at least", min_count, "examples.\n")

    filtered_df = df[df[label_col].isin(label_counts[label_counts >= min_count].index)]
    print(f"\nDataset size after filtering: {len(filtered_df)} rows\n")

    return filtered_df, rare_labels.index.tolist()
