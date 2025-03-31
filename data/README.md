# Near Earth Object Prediction System

This repository contains a comprehensive machine learning pipeline for predicting Near Earth Object (NEO) approach velocities using advanced neural network models. The system collects data from NASA's API, processes it into a format suitable for deep learning, trains both LSTM and Transformer models, and deploys them as a scalable API service on AWS.

## Project Overview

Near Earth Objects (NEOs) are asteroids and comets that have been nudged by the gravitational attraction of nearby planets into orbits that allow them to enter the Earth's neighborhood. This project aims to predict the relative velocities of these objects during their approaches to Earth by leveraging historical approach data and detailed orbital parameters.

The workflow includes:

1. **Data Collection**: Python pipeline to fetch comprehensive NEO data from NASA's API
2. **Data Processing**: Transformation of raw data into time series format suitable for neural networks
3. **Feature Engineering**: Creation of additional features from orbital mechanics
4. **Model Training**: Implementation and optimization of LSTM and Transformer neural networks
5. **Model Evaluation**: Comparative analysis of model performance against baseline methods
6. **API Development**: Flask-based RESTful API for inference
7. **Containerization**: Docker packaging of models and dependencies
8. **Cloud Deployment**: Serverless deployment on AWS using ECR and ECS Fargate

## Repository Structure
.
├── src/
│   ├── fetch_neo.py          # Data collection from NASA API
│   ├── model_building.ipynb  # Model training notebook
│   └── data_utils.py         # Data processing utilities
├── aws_deployment/
│   ├── Dockerfile            # Container definition
│   ├── inference.py          # Flask API implementation
│   ├── test_api.py           # Local API testing utility
│   ├── test_api_external.py  # Remote API testing utility
│   └── deploy_to_aws.py      # AWS deployment script
├── data/                     # Data storage (not tracked in git)
│   ├── neo/
│   │   ├── raw/              # Raw NEO data
│   │   ├── aggregations/     # Aggregated statistics
│   │   └── orbital/          # Orbital parameters
├── config.json               # Configuration file for API keys (not tracked)
├── requirements.txt          # Python dependencies
├── Dockerfile                # Docker for development environment
├── docker-compose.yml        # Docker-compose for development environment
├── README.md                 # This file
└── .gitignore                # Git ignore configuration

## Getting Started

### Prerequisites

- Python 3.11 or higher
- NASA API key (obtain one from [api.nasa.gov](https://api.nasa.gov/))
- Docker (for containerization)
- AWS CLI configured with appropriate permissions (for deployment)
- GPU recommended for faster model training

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/neo-prediction-system.git
   cd neo-prediction-system

Create a virtual environment and install dependencies:
bashCopypython -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt

Create a config.json file in the root directory with your NASA API key:
jsonCopy{
  "api_key": "YOUR_NASA_API_KEY"
}


### Data Collection
The data collection script fetches NEO data from NASA's API and processes it into the required format:

Run the data collection script:
bashCopycd src
python fetch_neo.py

By default, the script collects data for 200 NEOs. You can specify a larger number:
bashCopypython fetch_neo.py 5000

The script creates three types of files:

NEO basic data (dimensions, hazard assessment, etc.)
Close approach data (dates, distances, velocities)
Orbital parameters (21 orbital characteristics)



The data is saved in Parquet format for efficient storage and fast loading during model training.
Data Processing and Feature Engineering
The data processing pipeline transforms the raw NEO data into a format suitable for time series modeling:

Sequence Creation: Each NEO's approach history is converted into a sequence of approaches
Temporal Features: Extraction of date components (year, month, day, day of year)
Derived Features: Calculation of derived features like diameter ratio and orbit ratio
Normalization: Standardization of all numeric features
Target Variable: The relative velocity of each approach is used as the prediction target

### Model Architecture
LSTM Model
The Long Short-Term Memory (LSTM) model is designed to capture temporal patterns in the approach sequences:

Two-layer LSTM with 128 hidden units
Separate pathway for processing static NEO features
Feature fusion through concatenation and fully connected layers
Dropout regularization to prevent overfitting

Transformer Model
The Transformer model leverages self-attention mechanisms to identify relationships between time steps:

Multi-head self-attention with 4 attention heads
Positional encoding to maintain sequence order information
Separate feature embedding pathway for static NEO characteristics
Feature fusion through concatenation and fully connected layers
Dropout regularization to prevent overfitting

### Training Process
The training process includes several optimization strategies:

Data Splitting: Chronological split with pre-2020 for training, 2020-2024 for validation, post-2025 for testing
Initialization: Xavier initialization for better gradient flow
Learning Rate Scheduling: OneCycleLR scheduler for faster convergence
Gradient Clipping: Prevention of exploding gradients
Early Stopping: Monitoring validation loss to prevent overfitting
Batch Normalization: Stabilization of training
Hyperparameter Tuning: Grid search for optimal configurations

### Model Performance
Both models significantly outperform baseline approaches:
ModelMean Absolute ErrorTraining Time per EpochParametersMean Baseline6.68 km/sN/AN/APersistence16.23 km/sN/AN/ALSTM0.385 (normalized)~13 seconds165,633Transformer0.396 (normalized)~15 seconds173,825
The LSTM model achieved slightly better performance with greater training stability, while the Transformer model showed higher volatility between epochs.
API Implementation
The Flask-based API provides a production-ready interface for model inference:

### Endpoints

#### Health Check: GET /health

Confirms the API is operational and models are loaded
Example response:
jsonCopy{
  "status": "healthy",
  "models_loaded": ["lstm", "transformer"]
}



#### Model Information: GET /models

Provides metadata about available models and required inputs
Example response:
jsonCopy{
  "available_models": ["lstm", "transformer"],
  "metadata": {
    "feature_names": ["miss_distance_km", "relative_velocity_kps", "approach_year", ...],
    "sequence_length": 2,
    "num_features": 6,
    "target_dims": 1
  }
}



#### Prediction: POST /predict

Makes velocity predictions based on sequence and feature data
Request body:
jsonCopy{
  "model": "transformer",
  "sequence": [
    [0.1, 15.2, 2022, 6, 15, 166],
    [0.08, 16.4, 2023, 8, 22, 234]
  ],
  "features": [0.85, 0.21, 0.46, 0.12, 0.78, 0.35, 0.92, 0.64, 0.53, 0.29, 0.71, 0.43, 0.18, 0.87, 0.57, 0.32, 0.69, 0.41, 0.26, 0.63, 0.72, 0.39]
}

Example response:
jsonCopy{
  "model": "transformer",
  "prediction": [[14.95]],
  "scaled_prediction": [[0.423]]
}

### Deployment Steps

#### Build and tag the Docker image:
bashCopycd aws_deployment
docker build -t neo-predictor .

#### Authenticate with Amazon ECR:
bashCopyaws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 955855778558.dkr.ecr.us-west-2.amazonaws.com

#### Tag and push the image to ECR:
bashCopydocker tag neo-predictor:latest 955855778558.dkr.ecr.us-west-2.amazonaws.com/neo-predictor-repo:latest
docker push 955855778558.dkr.ecr.us-west-2.amazonaws.com/neo-predictor-repo:latest

#### Deploy to ECS Fargate:
bashCopypython deploy_to_aws.py

#### Test the deployed API:
bashCopypython test_api_external.py

#### Monitoring and Scaling
The AWS deployment includes:
* CloudWatch metrics for CPU, memory, and request latency
* Auto-scaling based on request load
* Health checks to ensure availability
* Proper IAM roles and permissions for security



## Performance Evaluation
The models were evaluated using several metrics:
LSTM Performance

Training Loss: 0.362
Validation Loss: 0.385
Test Loss: 0.412
Mean Absolute Error: 3.78 km/s (unscaled)
R² Score: 0.823

Transformer Performance

Training Loss: 0.367
Validation Loss: 0.396
Test Loss: 0.419
Mean Absolute Error: 3.92 km/s (unscaled)
R² Score: 0.814

## Learning Curves
The learning curves showed:

Both models converged within 15 epochs
The LSTM had a more stable training trajectory
The Transformer showed more oscillation between epochs
Early stopping typically triggered around epoch 12-13

## License

This project is licensed under the MIT License - see the LICENSE file for details.