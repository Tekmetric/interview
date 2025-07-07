# 🔧 Automotive Repair Classifier

This project is a production-ready system for **automatically classifying automotive repair titles** into a predefined parts taxonomy. It supports both **local embedding-based matching using FAISS** and **agentic LLM-based reasoning using Ollama**.

---

## 🚀 Features

- **Multi-mode classification**: Choose between fast embedding similarity and local LLM reasoning.
- **Production-scale ready**: Designed for high-throughput use cases (up to 10M items/day).
- **Supports caching, thresholding, and top-N selection**
- **CSV upload and single-sentence UI**
- **Modular, configurable, and containerizable**

---

## 🖥️ System Requirements

Developed and tested on:

- **macOS Sequoia 15.5**
- **Apple M4 Max Pro chip**
- **Python 3.12**

> ⚠️ This system uses local embeddings and local LLM inference. Please ensure your hardware has enough memory and CPU performance to run `sentence-transformers` and Ollama models efficiently.

---

## 🧠 How It Works

- Input: A sentence like `"Replacing bulb for front right turning light"`
- Output: A predicted section and part name from `tax.csv` (e.g., `Lighting -> Exterior Bulb`), or `unknown` if nothing matches.

---

## 📁 File Overview

| File              | Purpose |
|-------------------|---------|
| `app.py`          | Main Gradio UI for sentence or CSV-based classification |
| `agent_llm.py`    | Agentic classifier using local LLM (Ollama) |
| `classifier.py`   | Embedding-based classifier using FAISS and Sentence Transformers |
| `evaluate.py`     | Optional evaluator script (not used in main UI) |
| `Dockerfile`      | Container config for deployment |
| `requirements.txt`| Full dependency list |
| `tax.csv`         | Your parts taxonomy file |
| `config.yaml`     | Configuration settings |
| `small_dataset.csv` | Optional sample file for LLM testing |
| `results/`        | Output CSVs saved with timestamped filenames |

---

## 🔧 Configuration

All behaviors are controlled via `config.yaml`:

```yaml
# Classifier behavior
top_n: 5
threshold: 0.5
mode: "top_n"  # or "threshold"

# Input handling
field_name: "title"

# Embedding / FAISS
embedding_model: "all-MiniLM-L6-v2"
faiss_index_path: null  # rebuild in memory

# LLM settings
batch_size: 3
ollama_model: "mistral"
ollama_base_url: "http://localhost:11434"

# Output
output_dir: "results"
timestamp_output: true

# Logging
verbose: true
```

---

## 🧪 Running Locally (macOS)

1. **Install Python 3.12** (e.g. via pyenv)

2. **Clone and install requirements**

```bash
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

3. **Install Ollama**

```bash
brew install ollama
ollama serve
ollama run mistral
```

4. **Start the app**

```bash
python app.py
```

Then visit [http://localhost:7860](http://localhost:7860)

---

## 🐳 Run via Docker (Cross-platform)

> Requires local Ollama running on host at `localhost:11434`

```bash
docker build -t repair-classifier .
docker run --rm -p 7860:7860 repair-classifier 
```

Then visit [http://localhost:7860](http://localhost:7860)

---

## 📥 Inputs and Outputs

### Modes:
- `Embedding`: Uses fast cosine similarity over FAISS
- `LLM`: Uses local reasoning from Mistral via Ollama

### Inputs:
- Single-sentence: Type in a repair title
- CSV file: Must contain a column matching `field_name` in config (`title`)

### Outputs:
- `results/classified_results_<timestamp>.csv`
- `results/no_matches_<timestamp>.csv` (only created if unknown matches exist)

---

## 🧪 Testing Notes

- The `small_dataset.csv` file is included to let you **test LLM mode quickly**, especially useful on slower hardware or when using larger Ollama models.
- LLM batch size is configurable to prevent overload.

---

## ✅ Current Capabilities

- ✅ Top-N and threshold mode
- ✅ LLM and Embedding classifiers
- ✅ Caching layer
- ✅ Config-driven behavior
- ✅ Docker-compatible
- ✅ Robust "unknown" fallback
- ✅ Timestamped result saving

---

## 📌 TODO / Extensions

- [ ] Persist embedding cache across runs
- [ ] Improve LLM prompt refinement
- [ ] Add evaluation metrics to UI
