# Auto Repair Title Classifier

This project implements a multi-label classifier for assigning repair order titles to a structured taxonomy of section-name pairs.

It combines a traditional ML classifier with a semantic similarity-based fallback using MiniLM embeddings to handle unknown or low-confidence inputs.

## Project Highlights

### Sophisticated Design, Not Just Accuracy

While final accuracy may be modest due to the small dataset (201 training rows across 12 labels), this system is built to **showcase thoughtful engineering decisions and extensibility**, rather than just scores.

### Dataset notes

Only 12 of the 45 taxonomy labels are represented in the dataset, and one of those only appears once. To avoid creating a system only capable of predicting those 12 known classes, we use semantic similarity fallback as a state between classification and the default "unknown" class. Stratification of the dataset for train / test splits addresses the strong imbalance (2 labels correspond to a massive proportion of the dataset titles), and because of both these things, the label with only one row in the dataset has been temporarily removed.

Note: For classification, single labels were created by joining source and name with '::' - this also enables flexible downstream classification use, as splitting on '::' will separate predictions back into their two components for database storage, UI fields, etc.

### Open-Set Classification with Fallback

- If the ML classifier is confident: it returns a known label.
- If the confidence is below a threshold: we compute cosine similarity between the input and label embeddings using MiniLM.
- If similarity is still too low: we return `'unknown'`.

This mirrors real-world robustness in production systems where not all input belongs to a known class.

### Tunable Thresholds via Grid Search

We run grid search over both:

- **Classifier confidence threshold** (e.g. 0.6, 0.7, etc.)
- **Fallback similarity threshold** (e.g. 0.6, 0.7, etc.)

This allows fine-grained control over how conservative or exploratory the system is.

### Semantic Predictions

The fallback model surfaces label candidates based on sentence-transformer similarity, allowing intelligent predictions even when the classifier fails.

Example fallback predictions:

```
Input: "camshaft cracked"
→ Classifier: low confidence
→ Fallback: "Engine :: Camshaft"
```

### Performance Metrics: Macro-F1

Macro-F1 is an ideal metric (vs accuracy, precision, recall) for this project, because the dataset has very imbalanced classes. We don't want to only see that the system performs well on easy targets. Macro-F1 is a measure of the unweighted average of F1 scores for each class:

F1 = 2 * (precision * recall) / (precision + recall)

Macro-F1 = Average(F1_class_1, F1_class_2, ..., F1_class_N)

In other words, because our taxonomy has a few very common labels and many rare ones (rare meaning not seen at all in the training dataset), macro-F1 avoids rewarding the model for only doing well on the majority class - and gives us a more detailed overall view that will communicate improvements clearly over time.

---

### Logging for Transparency

The system logs:

- When fallback is used
- What it returns
- How often it returns 'unknown'
- Sample fallback predictions

This is meant to be **interview-friendly**: it shows reviewers the system's behavior clearly.
In production, logging would be done using something more sophisticated than print statements (although these are allowed in AWS lambdas)

---

## Files

- `train_and_tune.py` — Runs training + grid search for best thresholds
- `final_train.py` — Trains final model on full dataset
- `classify.py` — Runs a single classification (uses fallback if needed)
- `config.py` — Central config with threshold and path control
- `src/` — Source code modules (classifier, fallback matcher, embedding logic, etc.)

---

## Key Decisions (and Why They Matter)

| Decision                                      | Reason                                                     |
| --------------------------------------------- | ---------------------------------------------------------- |
| Use fallback with MiniLM                      | Better handling of inputs not seen during training         |
| Tune thresholds via macro-F1                  | Appropriate for imbalanced classes                         |
| Persist label embeddings                      | Avoid re-computing for every prediction                    |
| Structured config and joblib model versioning | Keeps system modular and reproducible                      |
| Keep fallback threshold low for now           | Shows off semantic predictions for interview demo purposes |

---

### System Constraint Handling

| Requirement | How the System Addresses It |
|-------------|------------------------------|
| **10M items per day** | The classifier uses fast, memory-efficient models like TF-IDF + Logistic Regression or LinearSVC. These models are ideal for high-throughput batch or real-time inference at scale. |
| **40% reprocessing of titles** | This system can be extended to include a **classification results index**:  <br>– For simple setups: in-memory **LRU cache** or local **SQLite**  <br>– In production: use **Redis**, **DynamoDB**, or **OpenSearch** to store results keyed by normalized title hashes  <br>– Prevents redundant computation and enables version-aware lookups |
| **$0.0005 per error** | Error cost is addressed by:  <br>– Thresholding low-confidence predictions  <br>– Semantic fallback using MiniLM  <br>– Macro-F1 optimization for imbalanced labels  <br>– In production, fallback usage and “unknowns” should be **logged to a structured store** for audit and retraining |
| **Quarterly taxonomy updates (5–7%)** | Label embeddings are versioned and re-generated without retraining the classifier.  <br>– In production, embeddings can be indexed in **Pinecone** or **OpenSearch** for semantic search and fast lookup. |
| **Variable title formats by shop** | The fallback matcher handles inconsistent phrasing using sentence-transformer embeddings.  <br>– Production-ready pipelines can combine:  <br>   → **OpenSearch** for fuzzy keyword/token match  <br>   → **Pinecone** for vector-based ANN retrieval  <br>   → Hybrid search for the best of both worlds |


---

## Next Steps for Production

If this were to scale:

- Use a model registry for versioned artifact loading
- Store predictions and fallback decisions in a log store
- Add human-in-the-loop validation or retraining loop
- Evaluate with more data and explore LLM-based few-shot fallback

---

## How to Use This Project

### 1. Environment Setup

Create a virtual environment and install dependencies:

```bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```


---

### 2. Train and Tune

This runs training on the dataset and finds optimal thresholds for classification and fallback.

```bash
python train_and_tune.py
```

---

### 3. Final Training

Once thresholds are tuned, retrain the final model with full data and save versioned artifacts:

```bash
python final_train.py
```

---

### 4. Run Sample Classifications

Use `classify_title()` from `classify.py`:

```python
from classify import classify_title

label, source = classify_title("replace camshaft")
print(f"Predicted: {label} (via {source})")
```

Or use the CLI version:

```bash
python classify.py --title "replace camshaft"
```

---

### 5. Encode Taxonomy Labels (Standalone)

You can regenerate label embeddings manually if needed:

```bash
python -c "import pandas as pd; from config import CONFIG; from utils.label_utils import build_label; from src.label_embedder import embed_and_save_labels; labels = pd.read_csv(CONFIG['data']['taxonomy_path']).apply(build_label, axis=1).tolist(); embed_and_save_labels(labels)"

```

---

### 6. Run Unit Tests

Tests are located in the `tests/` folder. To run all tests:

```bash
pytest
```

Or to run a specific test file:

```bash
pytest tests/test_preprocess.py
```

---

## Contact

This project was implemented by Sabrina Kent as part of an MLE technical interview exercise.

The goal was to demonstrate thoughtful design, not just metrics. I'm happy to walk you through any part of the system!

