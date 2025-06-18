import joblib
from sentence_transformers import SentenceTransformer

from config import CONFIG


def embed_and_save_labels(label_list):
    model = SentenceTransformer(CONFIG["fallback_matcher"]["embedding_model"])
    embeddings = model.encode(label_list)

    import os
    os.makedirs(os.path.dirname(CONFIG["fallback_matcher"]["label_embeddings_path"]), exist_ok=True)

    joblib.dump(
        {"labels": label_list, "embeddings": embeddings},
        CONFIG["fallback_matcher"]["label_embeddings_path"],
    )
    print("Saved label embeddings.")


def load_label_embeddings():
    data = joblib.load(CONFIG["fallback_matcher"]["label_embeddings_path"])
    return data["labels"], data["embeddings"]
