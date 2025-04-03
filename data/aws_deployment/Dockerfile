# Use the official Python 3.11 image as the base
FROM python:3.11-slim

# Set the working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    build-essential \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Upgrade pip first
RUN pip install --no-cache-dir --upgrade pip

# Install specific versions of packages that match your training environment
RUN pip install --no-cache-dir \
    torch==2.6.0 \
    numpy==2.2.4 \
    pandas==2.2.3 \
    flask==3.1.0 \
    scikit-learn==1.6.1 \
    joblib==1.4.2 \
    matplotlib==3.10.1

RUN pip install --no-cache-dir requests==2.32.3


# Create models directory
RUN mkdir -p /app/models

# Copy model files from the models directory
COPY models/*.pt /app/models/
COPY models/*.joblib /app/models/
COPY models/*.json /app/models/

# Copy Python scripts
COPY inference.py /app/inference.py
COPY test_api.py /app/test_api.py

# Expose the port
EXPOSE 5000

# Set environment variable for Flask
ENV FLASK_APP=inference.py

# Run the API
CMD ["python", "inference.py"]