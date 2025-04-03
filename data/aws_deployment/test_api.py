import requests
import json
import numpy as np
import time
import sys

def test_api(base_url="http://127.0.0.1:5000"):
    """Test all API endpoints"""
    print(f"Testing API at {base_url}")
    
    # Test health endpoint
    print("\n=== Testing Health Endpoint ===")
    try:
        response = requests.get(f"{base_url}/health", timeout=5)
        print(f"Status: {response.status_code}")
        print(f"Response: {response.json()}")
    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)
    
    # Test models endpoint
    print("\n=== Testing Models Endpoint ===")
    try:
        response = requests.get(f"{base_url}/models", timeout=5)
        print(f"Status: {response.status_code}")
        print(f"Response: {json.dumps(response.json(), indent=2)}")
        
        # Extract model metadata for prediction test
        metadata = response.json().get('metadata', {})
        feature_names = response.json().get('feature_names', [])
        available_models = response.json().get('available_models', [])
        
        print(f"Available models: {available_models}")
        print(f"Feature names: {feature_names}")
    except Exception as e:
        print(f"Error: {str(e)}")
        sys.exit(1)
    
    # Create test data
    print("\n=== Creating Test Data ===")
    np.random.seed(42)  # For reproducibility
    
    # Generate some random sequence data with a reasonable range
    seq_length = metadata.get('sequence_length', 2)
    num_features = metadata.get('num_features', 6)
    
    # Create a sequence with appropriate dimensions [seq_length, num_features]
    sequence_data = np.random.uniform(-1, 1, (seq_length, num_features)).tolist()
    
    # Generate a random NEO feature vector (22 features)
    neo_features = np.random.uniform(-1, 1, 22).tolist()
    
    # Display test data
    print(f"Sequence shape: {len(sequence_data)} time steps × {len(sequence_data[0])} features")
    print(f"NEO features: {len(neo_features)} values")
    
    # Test prediction with each model
    for model_name in available_models:
        print(f"\n=== Testing Prediction with {model_name.upper()} Model ===")
        
        # Prepare prediction request
        prediction_data = {
            "model": model_name,
            "sequence": sequence_data,  # [seq_length, num_features]
            "features": neo_features    # [feature_dim]
        }
        
        # Make the request
        start_time = time.time()
        try:
            response = requests.post(
                f"{base_url}/predict",
                json=prediction_data,
                timeout=10
            )
            elapsed_time = time.time() - start_time
            
            print(f"Status: {response.status_code}")
            print(f"Response time: {elapsed_time:.3f} seconds")
            
            if response.status_code == 200:
                result = response.json()
                print(f"Prediction: {result['prediction']}")
                print(f"Scaled prediction: {result['scaled_prediction']}")
            else:
                print(f"Error: {response.text}")
        except Exception as e:
            print(f"Request failed: {str(e)}")

    print("\nAPI tests completed!")

if __name__ == "__main__":
    # Use local API by default, but allow command-line override
    api_url = sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:5000"
    test_api(api_url)