import gradio as gr
import pandas as pd
import os
from datetime import datetime

from classifier import TaxonomyClassifier
from agent_llm import AgenticClassifier

MODE = "Embedding"

embedding_classifier = TaxonomyClassifier("tax.csv", config_path="config.yaml")
ag_classifier = AgenticClassifier("tax.csv", config_path="config.yaml")

def classify_single(sentence, mode):
    if mode == "Embedding":
        results = embedding_classifier.classify(sentence).copy()
    else:
        results = ag_classifier.classify(sentence)
    return results[["input", "section", "name", "similarity_score", "source"]]

def classify_csv(file, mode):
    df = pd.read_csv(file.name)
    field = embedding_classifier.config.get("field_name", "title")
    inputs = df[field].dropna().drop_duplicates().tolist()

    all_results = []
    no_matches = []

    if mode == "Embedding":
        for sentence in inputs:
            matches = embedding_classifier.classify(sentence).copy()

            if matches.empty or all(
                (row["section"].strip().lower() == "unknown" and row["name"].strip().lower() == "unknown")
                for _, row in matches.iterrows()
            ):
                no_matches.append({
                    "input": sentence,
                    "section": "unknown",
                    "name": "unknown",
                    "similarity_score": "-",
                    "source": "Embedding"
                })
            else:
                all_results.extend(matches.to_dict(orient="records"))
    else:
        batch_results = ag_classifier.classify_batch(inputs)
        for row in batch_results:
            if row["section"].strip().lower() == "unknown":
                no_matches.append(row)
            else:
                all_results.append(row)

    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    os.makedirs("results", exist_ok=True)
    out_path = f"results/classified_results_{timestamp}.csv"
    no_path = f"results/no_matches_{timestamp}.csv"

    pd.DataFrame(all_results).to_csv(out_path, index=False)
    if no_matches:
        pd.DataFrame(no_matches).to_csv(no_path, index=False)

    return pd.DataFrame(all_results + no_matches)

def update_mode(selected_mode):
    global MODE
    MODE = selected_mode
    return

with gr.Blocks() as app:
    gr.Markdown("# 🔍 Automotive Repair Classifier")

    with gr.Row():
        mode_selector = gr.Radio(["Embedding", "LLM"], label="Classification Mode", value="Embedding", interactive=True)
        mode_selector.change(fn=update_mode, inputs=mode_selector)

    with gr.Tab("🔎 Single Sentence"):
        text_input = gr.Textbox(label="Enter a repair sentence")
        output_table = gr.Dataframe(headers=["input", "section", "name", "similarity_score", "source"])
        classify_btn = gr.Button("Classify")
        classify_btn.click(classify_single, inputs=[text_input, mode_selector], outputs=output_table)

    with gr.Tab("📁 Classify CSV File"):
        file_input = gr.File(label="Upload CSV File")
        csv_output = gr.Dataframe()
        classify_csv_btn = gr.Button("Run Classification")
        classify_csv_btn.click(classify_csv, inputs=[file_input, mode_selector], outputs=csv_output)

app.launch(server_name="0.0.0.0", server_port=7860)