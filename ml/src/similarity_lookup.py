import torch
from sentence_transformers import SentenceTransformer, util

from config import CONFIG
from src.label_embedder import load_label_embeddings


class SimilarityMatcher:
    def __init__(self):
        # Force CPU for consistency and simplicity
        self.device = "cpu"

        # Load embedding model
        self.model = SentenceTransformer(CONFIG["fallback_matcher"]["embedding_model"])

        self.similarity_threshold = CONFIG["fallback_matcher"]["similarity_threshold"]
        self.return_unknown_if_no_match = CONFIG["fallback_matcher"][
            "return_unknown_if_no_match"
        ]
        self.unknown_label = CONFIG["output"]["unknown_label"]

        # Load and convert label embeddings to torch tensor on CPU
        try:
            self.labels, raw_embeddings = load_label_embeddings()
            self.embeddings = torch.tensor(raw_embeddings).to(self.device)
        except FileNotFoundError:
            raise RuntimeError(
                "Label embeddings file not found. Run label embedding setup before using SimilarityMatcher."
            )

        # Future option:
        # If we scale up to a GPU server, change self.device to "cuda" if available:
        # self.device = "cuda" if torch.cuda.is_available() else "cpu"

    def predict(self, input_title: str):
        # Embed input title and move to same device as stored embeddings
        title_embedding = self.model.encode(input_title, convert_to_tensor=True).to(
            self.device
        )

        # Compute cosine similarity
        cosine_scores = util.cos_sim(title_embedding, self.embeddings)[0]

        top_idx = cosine_scores.argmax().item()
        top_score = cosine_scores[top_idx].item()
        top_label = self.labels[top_idx]

        # temporary debug log
        # print(f"[DEBUG] Title: '{input_title}' | Top score: {top_score:.4f} | Top label: {top_label}")

        if top_score >= self.similarity_threshold:
            return top_label
        elif self.return_unknown_if_no_match:
            return self.unknown_label
        else:
            return None
