import pandas as pd
from classifier import TaxonomyClassifier
from sklearn.metrics import precision_recall_fscore_support
import gradio as gr

def to_label(section, name):
    return f"{section} - {name}"

def evaluate(csv_path, taxonomy_path="tax.csv", config_path="config.yaml"):
    df = pd.read_csv(csv_path)
    classifier = TaxonomyClassifier(taxonomy_path, config_path)

    y_true_all = []
    y_pred_all = []
    label_set = set()

    for _, row in df.iterrows():
        sentence = row["title"]
        true_label = to_label(row["section"], row["name"])
        label_set.add(true_label)

        preds = classifier.classify(sentence)
        pred_labels = set(preds["section"] + " - " + preds["name"])
        label_set.update(pred_labels)

        # One-hot encode
        for label in label_set:
            y_true_all.append(1 if label == true_label else 0)
            y_pred_all.append(1 if label in pred_labels else 0)

    precision, recall, f1, _ = precision_recall_fscore_support(
        y_true_all, y_pred_all, average='binary'
    )

    metrics = {
        "Precision": round(precision, 3),
        "Recall": round(recall, 3),
        "F1 Score": round(f1, 3)
    }

    return pd.DataFrame([metrics])

with gr.Blocks() as eval_ui:
    gr.Markdown("## 📊 Evaluate Classifier Against Ground Truth")
    eval_file = gr.File(label="Upload Labeled CSV")
    results_df = gr.Dataframe(label="Evaluation Metrics")
    eval_button = gr.Button("Run Evaluation")
    eval_button.click(fn=evaluate, inputs=eval_file, outputs=results_df)

eval_ui.launch()

if __name__ == "__main__":
    eval_ui.launch()
