from config import CONFIG


def build_label(row):
    """Given a row, combine the section and name into a single string with :: as delimiter"""
    columns_to_combine = CONFIG["labeling"]["source_fields"]
    label_delimiter = CONFIG["labeling"]["separator"]

    parts = [
        str(row[field]) for field in columns_to_combine
    ]  # lists of column strings for row
    # join the column strings into one using delimiter to make downstream splitting easy
    return label_delimiter.join(parts)
