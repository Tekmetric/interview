CONFIG = {
    # === Data paths ===
    "data": {
        "taxonomy_path": "data/taxonomy.csv",
        "training_data_path": "data/dataset.csv",
        "output_vectorizer_path": "models/vectorizer.pkl",
        "output_model_path": "models/classifier.pkl",
        "output_encoder_path": "models/label_encoder.pkl",
    },
    # === Classifier Settings ===
    "model": {
        "classifier_type": "Linear SVC with CalibratedClassifierCV",
        "use_tfidf": True,
        "max_features": 1000,
        "confidence_threshold": 0.3,  # Below this, fallback or 'unknown'
        "predict_unknown_if_below_threshold": True,
    },
    # === Training Parameters ===
    "training": {"test_size": 0.2, "random_state": 42},
    # === Model Versioning ===
    "model_versioning": {
        "use_timestamp": True,
        "timestamp_format": "%Y%m%d_%H%M%S",
        "model_dir": "models",
        "file_naming": {
            "classifier": "classifier_{version}.pkl",
            "vectorizer": "vectorizer_{version}.pkl",
            "encoder": "label_encoder_{version}.pkl",
        },
    },
    "model_loading": {"version": "latest"},
    # === Fallback Cosine Similarity Matcher ===
    "fallback_matcher": {
        "use_fallback": True,
        "embedding_model": "sentence-transformers/all-MiniLM-L6-v2",
        "similarity_threshold": 0.5,
        "reembed_labels": True,  # set to False to skip re-embedding during training
        "return_unknown_if_no_match": True,
        "label_embeddings_path": "models/label_embeddings.pkl",
    },
    # === Text Preprocessing ===
    "text": {
        "normalize_case": True,
        "remove_punctuation": True,
        "remove_stopwords": False,
        "custom_stopwords": [],
    },
    # === Label Configuration ===
    "labeling": {
        "label_field": "section_name",  # New column to create
        "source_fields": ["section", "name"],  # Columns to combine
        "separator": " :: ",  # Delimiter - symbol that won't be in section or name
    },
    # === Inference Behavior ===
    "inference": {
        "enable_caching": True,
        "cache_backend": "memory",  # options: 'memory', 'redis'
        "cache_ttl_seconds": 86400,  # 1 day
    },
    "output": {"unknown_label": "unknown"},
    # === Logging / Debugging ===
    "debug": {"verbose": True, "show_top_n_predictions": 3},
}
