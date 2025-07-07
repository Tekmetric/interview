import os
import json
import yaml
import hashlib
import faiss
import numpy as np
import pandas as pd
from sentence_transformers import SentenceTransformer

class TaxonomyClassifier:
    def __init__(self, taxonomy_path="tax.csv", config_path="config.yaml", label="Embedding"):
        self.taxonomy_path = taxonomy_path
        self.config_path = config_path
        self.label = label

        self.taxonomy = pd.read_csv(taxonomy_path)
        with open(config_path, "r") as f:
            self.config = yaml.safe_load(f)

        self.embedding_model_name = self.config.get("embedding_model", "all-MiniLM-L6-v2")
        self.embedding_model = SentenceTransformer(self.embedding_model_name)

        self.index_path = "index.faiss"
        self.embedding_cache_path = "embeddings.json"
        self.index = None
        self.embeddings = None

        self._load_or_build_index()

    def _hash_config(self):
        config_str = json.dumps(self.config, sort_keys=True)
        return hashlib.md5(config_str.encode()).hexdigest()

    def _load_or_build_index(self):
        rebuild_needed = False

        # Check if embedding cache and index exist
        if not os.path.exists(self.embedding_cache_path) or not os.path.exists(self.index_path):
            rebuild_needed = True
        else:
            with open(self.embedding_cache_path, "r") as f:
                cache = json.load(f)
                if cache.get("config_hash") != self._hash_config():
                    rebuild_needed = True

        if rebuild_needed:
            print("📦 Rebuilding taxonomy index...")
            self.embeddings = self._generate_embeddings(self.taxonomy)
            self._save_embedding_cache()
            self.index = self._build_faiss_index(self.embeddings)
            faiss.write_index(self.index, self.index_path)
        else:
            print("📂 Loading cached taxonomy index...")
            self.index = faiss.read_index(self.index_path)
            with open(self.embedding_cache_path, "r") as f:
                cache = json.load(f)
                self.embeddings = cache["embeddings"]

    def _generate_embeddings(self, df):
        sentences = (df["section"] + " - " + df["name"]).tolist()
        vectors = self.embedding_model.encode(sentences, show_progress_bar=True, convert_to_numpy=True)
        faiss.normalize_L2(vectors)  # Normalize for cosine similarity
        return vectors

    def _save_embedding_cache(self):
        labels = (self.taxonomy["section"] + " - " + self.taxonomy["name"]).tolist()
        data = {
            "config_hash": self._hash_config(),
            "embeddings": self.embeddings.tolist(),
            "labels": labels
        }
        with open(self.embedding_cache_path, "w") as f:
            json.dump(data, f)

    def _build_faiss_index(self, vectors):
        index = faiss.IndexFlatIP(vectors.shape[1])
        index.add(np.array(vectors).astype("float32"))
        return index

    def classify(self, sentence):
        vec = self.embedding_model.encode([sentence], convert_to_numpy=True)
        faiss.normalize_L2(vec)
        k = self.config.get("top_n", 2)
        mode = self.config.get("mode", "top_n")
        threshold = self.config.get("threshold", 0.5)

        sims, indices = self.index.search(vec, k)
        results = []
        for score, idx in zip(sims[0], indices[0]):
            if mode == "threshold" and score < threshold:
                continue
            results.append({
                "input": sentence,
                "section": self.taxonomy.iloc[idx]["section"],
                "name": self.taxonomy.iloc[idx]["name"],
                "similarity_score": round(float(score), 4),
                "source": self.label
            })

        if not results:
            return pd.DataFrame([{
                "input": sentence,
                "section": "unknown",
                "name": "unknown",
                "similarity_score": "-",
                "source": self.label
            }])

        return pd.DataFrame(results)