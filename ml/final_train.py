import datetime
import os

import joblib
import pandas as pd
from sklearn.calibration import CalibratedClassifierCV
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import LinearSVC

from config import CONFIG
from src.label_embedder import embed_and_save_labels
from src.preprocess import preprocess_dataset
from utils.data_utils import filter_dataset_for_training
from utils.label_utils import build_label


def final_train():
    # Load best thresholds from config (they might have been changed with most recent train_and_tune run)
    try:
        with open("best_thresholds.txt", "r") as f:
            classifier_thresh, fallback_thresh = f.read().strip().split(",")
        CONFIG["model"]["confidence_threshold"] = float(classifier_thresh)
        CONFIG["fallback_matcher"]["similarity_threshold"] = float(fallback_thresh)
    except Exception:
        print("No best_thresholds.txt found — using default config values.")

    # Load and preprocess
    df = pd.read_csv(CONFIG["data"]["training_data_path"])
    df = preprocess_dataset(df)
    label_col = CONFIG["labeling"]["label_field"]

    print(f"Original dataset size: {len(df)} rows")
    # Filter out labels with fewer than 2 examples (for classifier + stratification stability)
    df, removed_labels = filter_dataset_for_training(df, label_col)

    if removed_labels:
        print(f"\nRemoved {len(removed_labels)} row from dataset during final train")

    # Encode labels
    encoder = LabelEncoder()
    y = encoder.fit_transform(df[label_col])

    # Vectorize
    vectorizer = TfidfVectorizer(
        max_features=CONFIG["model"]["max_features"], ngram_range=(1, 2)
    )
    X = vectorizer.fit_transform(df["clean_title"])

    print(f"Training final model with:")
    print(f"  - {len(df)} rows")
    print(f"  - {len(encoder.classes_)} labels")
    print(f"  - Confidence threshold: {CONFIG['model']['confidence_threshold']}")
    print(
        f"  - Fallback threshold: {CONFIG['fallback_matcher']['similarity_threshold']}"
    )
    print(f"Model type: {CONFIG['model']['classifier_type']}")

    # Train model
    svc = LinearSVC()
    clf = CalibratedClassifierCV(
        svc, cv=2
    )  # Adds probability estimates for thresholding
    clf.fit(X, y)

    # Only re-embed label taxonomy if explicitly flagged (usually only if taxonomy has changed)
    if CONFIG["fallback_matcher"].get("reembed_labels", False):
        print("Re-embedding taxonomy labels...")
        taxonomy = pd.read_csv(CONFIG["data"]["taxonomy_path"])
        label_list = taxonomy.apply(build_label, axis=1).tolist()
        embed_and_save_labels(label_list)
        print("Label embeddings saved.")
    else:
        print("Skipping label re-embedding per config.")

    # Save model, encoder, and vectorizer
    versioning_cfg = CONFIG["model_versioning"]
    model_dir = versioning_cfg["model_dir"]

    # Generate version string
    if versioning_cfg["use_timestamp"]:
        version = datetime.datetime.now().strftime(versioning_cfg["timestamp_format"])
    else:
        version = "latest"

    # Format file paths
    file_paths = {
        "classifier": os.path.join(
            model_dir,
            versioning_cfg["file_naming"]["classifier"].format(version=version),
        ),
        "vectorizer": os.path.join(
            model_dir,
            versioning_cfg["file_naming"]["vectorizer"].format(version=version),
        ),
        "encoder": os.path.join(
            model_dir, versioning_cfg["file_naming"]["encoder"].format(version=version)
        ),
    }

    # Save models
    joblib.dump(clf, file_paths["classifier"])
    joblib.dump(vectorizer, file_paths["vectorizer"])
    joblib.dump(encoder, file_paths["encoder"])

    print(f"Saved models with version: {version}")

    # Save latest versions
    joblib.dump(clf, os.path.join(model_dir, "classifier_latest.pkl"))
    joblib.dump(vectorizer, os.path.join(model_dir, "vectorizer_latest.pkl"))
    joblib.dump(encoder, os.path.join(model_dir, "label_encoder_latest.pkl"))


if __name__ == "__main__":
    final_train()
