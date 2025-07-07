import os
import pandas as pd
from classifier import TaxonomyClassifier
from ollama import Client

def chat_with_ollama(model, messages):
    host = os.environ.get("OLLAMA_BASE_URL", "http://localhost:11434")
    client = Client(host=host)  # ✅ only pass host, not base_url

    try:
        _ = client.list()  # health check
    except Exception as e:
        raise RuntimeError(f"🛑 Ollama health check failed at {host}: {e}")

    return client.chat(model=model, messages=messages)

class AgenticClassifier:
    def __init__(self, taxonomy_path="tax.csv", config_path="config.yaml", model="mistral"):
        self.embedding = TaxonomyClassifier(taxonomy_path, config_path, label="LLM")
        self.taxonomy = self.embedding.taxonomy
        self.model = model
        self.config = self.embedding.config
        self.batch_size = self.config.get("batch_size", 3)
        print(f"🤖 [LLM Agent] Initialized with model: {self.model}, batch_size: {self.batch_size}")

    def get_embedding_candidates(self, sentence):
        return self.embedding.classify(sentence)

    def prompt_llm_batch(self, batch, batch_candidates):
        print(f"📦 [LLM Batch] Processing batch size: {len(batch)}")
        prompt_lines = [
            "You are an intelligent assistant for automotive repair classification.",
            "You will be given a list of sentences (repair titles), each with a set of candidate labels from a standardized taxonomy.",
            "Each candidate label consists of a section and a name (example: Brakes - Disc Brake Caliper).",
            "",
            "Your task is to choose the **most appropriate** label for each sentence, using only the candidates provided.",
            "If none of the candidates are suitable, respond with `unknown - unknown`.",
            "",
            "IMPORTANT RULES:",
            "- Do NOT invent labels.",
            "- Do NOT alter the label names.",
            "- Pick exactly one candidate OR `unknown - unknown`.",
            "- Use this response format exactly: `1. section - name`",
            "",
            "Now classify the following:"
        ]
        for i, (sentence, candidates) in enumerate(zip(batch, batch_candidates), 1):
            taxonomy_block = "\n".join(
                f"    - {row['section']} - {row['name']} (score: {float(row['similarity_score']):.3f})"
                if isinstance(row["similarity_score"], (float, int))
                else f"    - {row['section']} - {row['name']} (score: {row['similarity_score']})"
                for _, row in candidates.iterrows()
            )
            prompt_lines.append(f"{i}. Sentence: \"{sentence}\"\nCandidates:\n{taxonomy_block}\n")

        prompt = "\n".join(prompt_lines)

        response = chat_with_ollama(model=self.model, messages=[
            {"role": "user", "content": prompt}
        ])

        return response["message"]["content"].strip()

    def classify_batch(self, sentences):
        results = []
        for i in range(0, len(sentences), self.batch_size):
            batch = sentences[i:i + self.batch_size]
            batch_candidates = [self.get_embedding_candidates(sentence) for sentence in batch]
            response_text = self.prompt_llm_batch(batch, batch_candidates)

            response_lines = response_text.split("\n")
            for j, line in enumerate(response_lines):
                if j >= len(batch):
                    continue
                sentence = batch[j]
                candidates = batch_candidates[j]
                try:
                    if "unknown" in line.lower():
                        results.append({
                            "input": sentence,
                            "section": "unknown",
                            "name": "unknown",
                            "similarity_score": "-",
                            "source": "LLM"
                        })
                    elif " - " in line:
                        _, label = line.split(".", 1)
                        section, name = label.strip().split(" - ", 1)
                        match_row = candidates[
                            (candidates["section"].str.strip() == section.strip()) &
                            (candidates["name"].str.strip() == name.strip())
                        ]
                        if not match_row.empty:
                            score = match_row["similarity_score"].iloc[0]
                            results.append({
                                "input": sentence,
                                "section": section.strip(),
                                "name": name.strip(),
                                "similarity_score": score,
                                "source": "LLM"
                            })
                        else:
                            fallback = candidates.sort_values("similarity_score", ascending=False).head(1)
                            if not fallback.empty:
                                results.append({
                                    "input": sentence,
                                    "section": fallback.iloc[0]["section"],
                                    "name": fallback.iloc[0]["name"],
                                    "similarity_score": fallback.iloc[0]["similarity_score"],
                                    "source": "Embedding fallback"
                                })
                            else:
                                results.append({
                                    "input": sentence,
                                    "section": "unknown",
                                    "name": "unknown",
                                    "similarity_score": "-",
                                    "source": "Embedding fallback"
                                })
                    else:
                        raise ValueError("Unrecognized format")
                except Exception:
                    results.append({
                        "input": sentence,
                        "section": "unknown",
                        "name": "unknown",
                        "similarity_score": "-",
                        "source": "LLM parse error"
                    })

        return results

    def classify(self, sentence):
        return pd.DataFrame(self.classify_batch([sentence]))