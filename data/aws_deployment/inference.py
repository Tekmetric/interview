import torch
import numpy as np
import os
import json
import joblib
from flask import Flask, request, jsonify
import logging

# Set up logging
logging.basicConfig(level=logging.INFO, 
                    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Flask application
app = Flask(__name__)

# Global variables
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
models = {}
seq_scaler = None
target_scaler = None
feature_scaler = None
metadata = None

# LSTM Model Definition
class LSTMModel(torch.nn.Module):
    def __init__(self, seq_dim, feature_dim, hidden_dim=128, output_dim=1, num_layers=2, dropout=0.2):
        super(LSTMModel, self).__init__()
        self.lstm = torch.nn.LSTM(
            input_size=seq_dim,
            hidden_size=hidden_dim,
            num_layers=num_layers,
            batch_first=True,
            dropout=dropout if num_layers > 1 else 0
        )
        
        # Feature processing - use the same layer names as in training
        self.fc_features = torch.nn.Linear(feature_dim, hidden_dim)
        
        # Combined processing - use the same layer names as in training
        self.fc_combined = torch.nn.Linear(hidden_dim * 2, hidden_dim)
        self.dropout = torch.nn.Dropout(dropout)
        self.fc_out = torch.nn.Linear(hidden_dim, output_dim)
        
    def forward(self, sequence, features):
        # Process sequence with LSTM
        lstm_out, (h_n, c_n) = self.lstm(sequence)
        
        # Use the last hidden state
        lstm_out = h_n[-1]  # Shape: [batch_size, hidden_dim]
        
        # Process static features
        feature_out = torch.relu(self.fc_features(features))  # Shape: [batch_size, hidden_dim]
        
        # Combine sequence and feature representations
        combined = torch.cat([lstm_out, feature_out], dim=1)  # Shape: [batch_size, hidden_dim * 2]
        
        # Final processing
        x = torch.relu(self.fc_combined(combined))
        x = self.dropout(x)
        x = self.fc_out(x)
        
        return x

# Transformer Model Definition - Updated to match the trained model
class TransformerModel(torch.nn.Module):
    def __init__(self, seq_dim, feature_dim, hidden_dim=128, output_dim=1, nhead=4, num_layers=2, dropout=0.2, max_len=10):
        super(TransformerModel, self).__init__()
        
        # Input embedding layer
        self.input_embedding = torch.nn.Linear(seq_dim, hidden_dim)
        
        # Positional encoding using the learned parameter
        self.positional_encoding = PositionalEncoding(hidden_dim, dropout, max_len=max_len)
        
        # Transformer encoder
        encoder_layers = torch.nn.TransformerEncoderLayer(
            d_model=hidden_dim, 
            nhead=nhead, 
            dim_feedforward=hidden_dim*4,
            dropout=dropout,
            batch_first=True
        )
        self.transformer_encoder = torch.nn.TransformerEncoder(
            encoder_layers, 
            num_layers=num_layers
        )
        
        # Feature processing
        self.feature_embedding = torch.nn.Linear(feature_dim, hidden_dim)
        
        # Combined processing - using the original layer names
        self.fc_combined = torch.nn.Linear(hidden_dim * 2, hidden_dim)
        self.dropout = torch.nn.Dropout(dropout)
        self.fc_out = torch.nn.Linear(hidden_dim, output_dim)
        
    def forward(self, sequence, features):
        # Embed input sequence
        x = self.input_embedding(sequence)
        
        # Add positional encoding
        x = self.positional_encoding(x)
        
        # Apply transformer encoder
        transformer_out = self.transformer_encoder(x)
        
        # Use the representation of the [CLS] token (first token)
        transformer_out = transformer_out[:, 0, :]  # Shape: [batch_size, hidden_dim]
        
        # Process static features
        feature_out = torch.relu(self.feature_embedding(features))  # Shape: [batch_size, hidden_dim]
        
        # Combine sequence and feature representations
        combined = torch.cat([transformer_out, feature_out], dim=1)  # Shape: [batch_size, hidden_dim * 2]
        
        # Final processing
        x = torch.relu(self.fc_combined(combined))
        x = self.dropout(x)
        x = self.fc_out(x)
        
        return x

# Positional Encoding class for the Transformer
class PositionalEncoding(torch.nn.Module):
    def __init__(self, d_model, dropout=0.1, max_len=10):
        super(PositionalEncoding, self).__init__()
        self.dropout = torch.nn.Dropout(p=dropout)
        
        # Create positional encoding matrix
        pe = torch.zeros(1, max_len, d_model)
        position = torch.arange(0, max_len, dtype=torch.float).unsqueeze(1)
        div_term = torch.exp(torch.arange(0, d_model, 2).float() * (-torch.log(torch.tensor(10000.0)) / d_model))
        
        pe[0, :, 0::2] = torch.sin(position * div_term)
        pe[0, :, 1::2] = torch.cos(position * div_term)
        
        # Register buffer to preserve during state_dict serialization
        self.register_buffer('pe', pe)
        
    def forward(self, x):
        # Add positional encoding and apply dropout
        x = x + self.pe[:, :x.size(1), :]
        return self.dropout(x)

def load_models():
    """Load all saved models and preprocessing tools"""
    logger.info("Loading models and preprocessing tools...")
    
    global models, seq_scaler, target_scaler, feature_scaler, metadata
    
    try:
        # Load model metadata
        with open('models/model_metadata.json', 'r') as f:
            metadata = json.load(f)
            logger.info(f"Loaded model metadata: {metadata}")
        
        # Load scalers
        seq_scaler = joblib.load('models/seq_scaler.joblib')
        target_scaler = joblib.load('models/target_scaler.joblib')
        feature_scaler = joblib.load('models/feature_scaler.joblib')
        logger.info("Loaded scalers")
        
        # Model parameters from metadata
        seq_dim = metadata.get('num_features', 6)  # Number of features in each time step
        feature_dim = 22  # Updated to match trained model
        hidden_dim = 128
        output_dim = metadata.get('target_dims', 1)
        
        # Note: Use fixed size of 10 for max_len instead of getting from metadata
        max_len = 10  # Override metadata sequence_length
        
        # Initialize models with correct architecture
        lstm_model = LSTMModel(
            seq_dim=seq_dim,
            feature_dim=feature_dim,
            hidden_dim=hidden_dim,
            output_dim=output_dim
        )
        
        transformer_model = TransformerModel(
            seq_dim=seq_dim,
            feature_dim=feature_dim,
            hidden_dim=hidden_dim,
            output_dim=output_dim,
            max_len=max_len  # Use fixed value
        )
        
        # Load model weights
        lstm_model.load_state_dict(torch.load('models/lstm_model.pt', map_location=device))
        transformer_model.load_state_dict(torch.load('models/transformer_model.pt', map_location=device))
        
        # Set models to evaluation mode
        lstm_model.eval()
        transformer_model.eval()
        
        # Add models to dictionary
        models['lstm'] = lstm_model
        models['transformer'] = transformer_model
        
        logger.info(f"Successfully loaded {len(models)} models")
        return True
    
    except Exception as e:
        logger.error(f"Error loading models: {str(e)}")
        return False

@app.route('/predict', methods=['POST'])
def predict():
    """Endpoint for making predictions with the loaded models"""
    try:
        # Get data from request
        data = request.json
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        # Extract input data
        sequence = data.get('sequence')
        features = data.get('features')  # Changed to 'features' (plural)
        model_name = data.get('model', 'transformer').lower()  # Default to transformer
        
        if not sequence:
            return jsonify({'error': 'No sequence data provided'}), 400
        
        if model_name not in models:
            return jsonify({'error': f'Model {model_name} not available', 
                           'available_models': list(models.keys())}), 400
        
        # Prepare input data
        # Convert and reshape sequence data to [batch_size, seq_length, features]
        sequence_array = np.array(sequence)
        
        # Make sure sequence has proper dimensions [batch_size, seq_length, num_features]
        if sequence_array.ndim == 2:  # If shape is [seq_length, num_features]
            sequence_array = np.expand_dims(sequence_array, axis=0)  # Add batch dimension
        
        # Scale sequence data
        if seq_scaler:
            # Flatten for scaling
            batch_size, seq_length, feat_dim = sequence_array.shape
            sequence_flat = sequence_array.reshape(-1, feat_dim)
            sequence_scaled_flat = seq_scaler.transform(sequence_flat)
            sequence_scaled = sequence_scaled_flat.reshape(batch_size, seq_length, feat_dim)
        else:
            sequence_scaled = sequence_array
            
        # Convert features if provided (should be a 1D array of length 22)
        if features is not None:
            features_array = np.array([features])  # Add batch dimension: [1, 22]
            if feature_scaler:
                features_scaled = feature_scaler.transform(features_array)
            else:
                features_scaled = features_array
        else:
            # Default features value if not provided - zeros with correct shape
            features_scaled = np.zeros((1, 22))
        
        # Convert to PyTorch tensors
        sequence_tensor = torch.tensor(sequence_scaled, dtype=torch.float32).to(device)
        features_tensor = torch.tensor(features_scaled, dtype=torch.float32).to(device)
        
        # For transformer model - pad sequence to length 10 if model is transformer
        if model_name == 'transformer':
            # Get current sequence shape
            batch_size, seq_length, feat_dim = sequence_tensor.shape
            
            # If sequence length is less than 10, pad it
            if seq_length < 10:
                # Create padding tensor
                padding = torch.zeros((batch_size, 10 - seq_length, feat_dim), dtype=torch.float32).to(device)
                # Concatenate with original tensor
                sequence_tensor = torch.cat([sequence_tensor, padding], dim=1)
        
        # Make prediction
        with torch.no_grad():
            output = models[model_name](sequence_tensor, features_tensor)
            
        # Convert prediction back to numpy and inverse scale
        prediction_array = output.cpu().numpy()
        if target_scaler:
            prediction_original = target_scaler.inverse_transform(prediction_array)
        else:
            prediction_original = prediction_array
        
        # Return results
        return jsonify({
            'model': model_name,
            'prediction': prediction_original.tolist(),
            'scaled_prediction': prediction_array.tolist()
        })
    
    except Exception as e:
        logger.error(f"Prediction error: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/models', methods=['GET'])
def get_models():
    """Return information about available models"""
    return jsonify({
        'available_models': list(models.keys()),
        'metadata': metadata,
        'feature_names': metadata.get('feature_names', []) if metadata else []
    })

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint for monitoring"""
    if models and seq_scaler and target_scaler:
        return jsonify({'status': 'healthy', 'models_loaded': list(models.keys())})
    else:
        return jsonify({'status': 'unhealthy', 'reason': 'Models or scalers not loaded'}), 503

if __name__ == '__main__':
    # Create models directory if it doesn't exist
    os.makedirs('models', exist_ok=True)
    
    # Load models on startup
    success = load_models()
    
    if success:
        logger.info("Models loaded successfully, starting API server")
        # Get port from environment or use default
        port = int(os.environ.get('PORT', 5000))
        app.run(host='0.0.0.0', port=port)
    else:
        logger.error("Failed to load models, exiting")