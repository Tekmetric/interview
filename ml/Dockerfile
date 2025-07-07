# Base Python image
FROM python:3.12-slim

# Set environment variables
ENV PYTHONUNBUFFERED=1 \
    DEBIAN_FRONTEND=noninteractive \
    OLLAMA_BASE_URL=http://host.docker.internal:11434

# Install system-level dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    git \
 && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy requirements and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source files into the container
COPY . .

# Create results directory (for output files)
RUN mkdir -p /app/results

# Expose Gradio’s default port
EXPOSE 7860

# Default command to launch the app
CMD ["python", "app.py"]