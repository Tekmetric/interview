import numpy as np

from config import CONFIG
from src.preprocess import normalize_text
from src.similarity_lookup import SimilarityMatcher
from utils.model_loader import load_model_components

# Load correct versions of classifier, encoder, vectorizer
clf, vectorizer, encoder = load_model_components()

# Load fallback matcher if configured
fallback_enabled = CONFIG["fallback_matcher"]["use_fallback"]
matcher = SimilarityMatcher() if fallback_enabled else None


def classify_title(title):
    # handle bad input first
    if not title or not isinstance(title, str):
        return CONFIG["output"]["unknown_label"], "bad input unknown"

    clean_title = normalize_text(title)

    X = vectorizer.transform([clean_title])

    # Predict probabilities
    if hasattr(clf, "predict_proba"):
        proba = clf.predict_proba(X)[0]
        top_idx = np.argmax(proba)
        top_score = proba[top_idx]
    else:
        # For models without predict_proba, use decision function
        decision = clf.decision_function(X)[0]
        top_idx = np.argmax(decision)
        top_score = 1  # Can't evaluate confidence

    predicted_label = encoder.inverse_transform([top_idx])[0]

    # Apply threshold
    threshold = CONFIG["model"]["confidence_threshold"]
    predict_unknown = CONFIG["model"]["predict_unknown_if_below_threshold"]

    if top_score >= threshold:
        return predicted_label, "classifier"
    elif fallback_enabled and matcher:
        return matcher.predict(clean_title), "fallback"
    elif predict_unknown:
        return CONFIG["output"]["unknown_label"], "unknown"
    else:
        return None


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("--title", type=str, required=True, help="Title to classify")
    args = parser.parse_args()

    label, source = classify_title(args.title)
    print(f"Prediction: {label} (via {source})")
