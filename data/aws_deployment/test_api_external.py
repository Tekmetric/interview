# test_api_external.py
import requests
import json
import numpy as np
import time

base_url = "http://35.92.165.93:5000"

# Test health endpoint
print("\n=== Testing Health Endpoint ===")
try:
    response = requests.get(f"{base_url}/health", timeout=10)
    print(f"Status code: {response.status_code}")
    print(json.dumps(response.json(), indent=2))
except Exception as e:
    print(f"Error accessing health endpoint: {str(e)}")

# Test models endpoint
print("\n=== Testing Models Endpoint ===")
try:
    response = requests.get(f"{base_url}/models", timeout=10)
    print(f"Status code: {response.status_code}")
    print(json.dumps(response.json(), indent=2))
    
    # Extract information for predictions
    available_models = response.json().get('available_models', [])
    metadata = response.json().get('metadata', {})
except Exception as e:
    print(f"Error accessing models endpoint: {str(e)}")
    available_models = ["lstm", "transformer"]  # Fallback if endpoint fails
    metadata = {}

# Create test data
print("\n=== Creating Test Data ===")
np.random.seed(42)  # For reproducibility

# Generate test sequence data
seq_length = metadata.get('sequence_length', 2)  # Default to 2 time steps
num_features = metadata.get('num_features', 6)   # Default to 6 features

# Create a sequence with appropriate dimensions [seq_length, num_features]
sequence_data = np.random.uniform(-1, 1, (seq_length, num_features)).tolist()

# Generate a random NEO feature vector (22 features)
neo_features = np.random.uniform(-1, 1, 22).tolist()

print(f"Created test sequence: {len(sequence_data)} time steps × {len(sequence_data[0])} features")
print(f"Created test features vector with {len(neo_features)} values")

# Test prediction with each model
for model_name in available_models:
    print(f"\n=== Testing Prediction with {model_name.upper()} Model ===")
    
    # Prepare prediction request
    prediction_data = {
        "model": model_name,
        "sequence": sequence_data,  # [seq_length, num_features]
        "features": neo_features     # [feature_dim]
    }
    
    # Make the request
    start_time = time.time()
    try:
        response = requests.post(
            f"{base_url}/predict",
            json=prediction_data,
            timeout=30  # Longer timeout for external API
        )
        
        elapsed_time = time.time() - start_time
        
        print(f"Status code: {response.status_code}")
        print(f"Response time: {elapsed_time:.3f} seconds")
        
        if response.status_code == 200:
            result = response.json()
            print("Prediction results:")
            print(f"  Model: {result.get('model')}")
            
            # Print predictions in a readable format
            prediction = result.get('prediction')
            if prediction:
                if isinstance(prediction[0], list):  # Nested list
                    print(f"  Prediction (first row): {prediction[0]}")
                else:  # Single list
                    print(f"  Prediction: {prediction}")
                    
            # Print scaled predictions if available
            scaled_prediction = result.get('scaled_prediction')
            if scaled_prediction:
                if isinstance(scaled_prediction[0], list):  # Nested list
                    print(f"  Scaled prediction (first row): {scaled_prediction[0]}")
                else:  # Single list
                    print(f"  Scaled prediction: {scaled_prediction}")
        else:
            print(f"Error response: {response.text}")
            
    except Exception as e:
        print(f"Request failed: {str(e)}")

print("\n=== API Testing Completed ===")