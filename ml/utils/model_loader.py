import os

import joblib

from config import CONFIG


def load_model_components():
    version = CONFIG["model_loading"]["version"]
    model_dir = CONFIG["model_versioning"]["model_dir"]
    naming = CONFIG["model_versioning"]["file_naming"]

    def build_path(role):
        filename = naming[role].format(version=version)
        return os.path.join(model_dir, filename)

    clf_path = build_path("classifier")
    vec_path = build_path("vectorizer")
    enc_path = build_path("encoder")

    if not all(os.path.exists(p) for p in [clf_path, vec_path, enc_path]):
        raise FileNotFoundError(
            f"Could not find all model components for version '{version}'."
        )

    clf = joblib.load(clf_path)
    vectorizer = joblib.load(vec_path)
    encoder = joblib.load(enc_path)

    return clf, vectorizer, encoder
