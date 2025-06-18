import re

import pandas as pd

from config import CONFIG
from utils.label_utils import build_label


def normalize_text(text):
    if CONFIG["text"]["normalize_case"]:
        text = text.lower()
    if CONFIG["text"]["remove_punctuation"]:
        text = re.sub(r"[^\w\s]", "", text)
    return text.strip()


def preprocess_dataset(df):
    # Normalize titles
    df["clean_title"] = df["title"].apply(normalize_text)

    # we could convert labels to lower case also, but there's no need for this training set:
    # all labels are consistently first-char-capitalized. There's no danger of "Engine::Cylinder Head"
    # and "engine::cylinder head" being 2 different labels. That lets us avoid the complexity of
    # post-processing lables to revert capitalization changes; we'll only split section from name

    # Build combined label - "section_string::name_string" and store in new df column
    label_field = CONFIG["labeling"]["label_field"]
    df[label_field] = df.apply(build_label, axis=1)

    return df
