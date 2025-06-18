import os
from itertools import product

import numpy as np
import pandas as pd
from sklearn.calibration import CalibratedClassifierCV
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import MultinomialNB
from sklearn.preprocessing import LabelEncoder
from sklearn.svm import LinearSVC

from config import CONFIG
from src.label_embedder import embed_and_save_labels
from src.preprocess import preprocess_dataset
from src.similarity_lookup import SimilarityMatcher
from utils.config_updater import update_thresholds_in_config
from utils.data_utils import filter_dataset_for_training
from utils.label_utils import build_label


def train_and_tune():
    # Load and preprocess
    df = pd.read_csv(CONFIG["data"]["training_data_path"])
    df = preprocess_dataset(df)
    label_col = CONFIG["labeling"]["label_field"]

    print(f"Original dataset size: {len(df)} rows")
    # Filter out labels with fewer than 2 examples (for classifier + stratification stability)
    df, removed_labels = filter_dataset_for_training(df, label_col)

    if removed_labels:
        print(f"\nRemoved {len(removed_labels)} row from dataset during train and tune")

    # Train/test split
    train_df, test_df = train_test_split(
        df,
        test_size=CONFIG["training"]["test_size"],
        stratify=df[label_col],
        random_state=CONFIG["training"]["random_state"],
    )

    # Encode labels
    encoder = LabelEncoder()
    y_train = encoder.fit_transform(train_df[label_col])

    # Vectorize
    vectorizer = TfidfVectorizer(
        max_features=CONFIG["model"]["max_features"], ngram_range=(1, 2)
    )
    X_train = vectorizer.fit_transform(train_df["clean_title"])
    X_test = vectorizer.transform(test_df["clean_title"])

    # Train model with Logistic Regression
    # clf = LogisticRegression(max_iter=1000)
    # Or train model with Multinomial Naive Bayes
    # clf = MultinomialNB()
    # But for best results use Linear SVC with CalibratedClassifier CV (see notebook for explanation)
    svc = LinearSVC()
    clf = CalibratedClassifierCV(
        svc, cv=2
    )  # Adds probability estimates for thresholding
    clf.fit(X_train, y_train)

    # Measure classifier-only baseline for comparison
    print("\nBaseline: classifier only (no thresholding or fallback)")

    y_pred_baseline = encoder.inverse_transform(clf.predict(X_test))
    true_labels = test_df[label_col].tolist()

    baseline_report = classification_report(
        true_labels, y_pred_baseline, output_dict=True, zero_division=0
    )
    baseline_macro_f1 = baseline_report["macro avg"]["f1-score"]

    print(f"Baseline Macro-F1: {baseline_macro_f1:.4f}")
    # Report accuracy (non-ideal metric) for comparison with macro-f1
    baseline_accuracy = accuracy_score(true_labels, y_pred_baseline)
    print(f"Baseline Accuracy (don't trust it! For demo only): {baseline_accuracy:.4f}")

    # --- Check and generate label embeddings if needed ---
    embeddings_path = CONFIG["fallback_matcher"]["label_embeddings_path"]
    if not os.path.exists(embeddings_path):
        print(f"Label embeddings not found at {embeddings_path} — generating now.")
        taxonomy = pd.read_csv(CONFIG["data"]["taxonomy_path"])
        label_list = taxonomy.apply(build_label, axis=1).tolist()
        embed_and_save_labels(label_list)

    # Tune both thresholds and update their saved values in config
    matcher = SimilarityMatcher()
    unknown_label = CONFIG["output"]["unknown_label"]

    best_macro_f1 = 0
    best_combo = None

    classifier_thresholds = [0.3, 0.4, 0.5, 0.6, 0.7, 0.75, 0.8]
    fallback_thresholds = [0.5, 0.6, 0.7, 0.75, 0.8]

    for ct, ft in product(classifier_thresholds, fallback_thresholds):
        matcher.similarity_threshold = ft
        y_pred = []

        print(f"\nClassifier @ {ct}, Fallback @ {ft}")
        fallback_calls = 0
        fallback_unknowns = 0
        fallback_predictions = []

        for _, row in test_df.iterrows():
            clean = row["clean_title"]
            X = vectorizer.transform([clean])
            proba = clf.predict_proba(X)[0]
            top_idx = np.argmax(proba)
            top_score = proba[top_idx]
            label = encoder.inverse_transform([top_idx])[0]

            if top_score >= ct:
                y_pred.append(label)
            else:
                fallback_calls += 1
                fallback = matcher.predict(clean)
                if fallback == unknown_label:
                    fallback_unknowns += 1
                fallback_predictions.append(fallback)
                y_pred.append(fallback if fallback else unknown_label)

        print(f"  → Fallback used: {fallback_calls} times")
        print(f"  → Fallback returned 'unknown': {fallback_unknowns} times")
        print(f"  → Sample fallback predictions: {set(fallback_predictions)}")

        true_labels = test_df[label_col].tolist()
        report = classification_report(
            true_labels, y_pred, output_dict=True, zero_division=0
        )
        macro_f1 = report["macro avg"]["f1-score"]
        accuracy = accuracy_score(true_labels, y_pred)

        print(
            f"Classifier @ {ct}, Fallback @ {ft} -> Macro-F1: {macro_f1:.4f}, Accuracy: {accuracy:.4f}"
        )
        if macro_f1 > best_macro_f1:
            best_macro_f1 = macro_f1
            best_combo = (ct, ft)

    if best_combo is not None:
        print(
            f"\nBest thresholds -> Classifier: {best_combo[0]}, Fallback: {best_combo[1]}, Macro-F1: {best_macro_f1:.4f}"
        )
        with open("best_thresholds.txt", "w") as f:
            f.write(f"{best_combo[0]},{best_combo[1]}\n")
        update_thresholds_in_config(best_combo[0], best_combo[1])
    else:
        print("\nWarning: No threshold combination improved upon the baseline.")
        print("Keeping existing config values and skipping threshold save.")


if __name__ == "__main__":
    train_and_tune()
