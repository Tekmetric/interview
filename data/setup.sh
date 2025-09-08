#!/bin/bash
# Setup script for NASA NEO Data Scraper

echo "🚀 Setting up NASA NEO Data Scraper..."

# Check if Python 3.12.6 is available
if ! command -v python3.12 &> /dev/null; then
    echo "❌ Python 3.12.6 not found. Please install Python 3.12.6 first."
    echo "   You can download it from: https://www.python.org/downloads/"
    exit 1
fi

# Create virtual environment
echo "📦 Creating virtual environment with Python 3.12.6..."
python3.12 -m venv .venv

# Activate virtual environment
echo "🔧 Activating virtual environment..."
source .venv/bin/activate

# Upgrade pip
echo "⬆️  Upgrading pip..."
pip install --upgrade pip

# Install dependencies
echo "📚 Installing dependencies..."
pip install -r requirements.txt

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "⚠️  Creating .env file template..."
    cp .env.example .env
    echo "📝 Please edit .env file and add your NASA API key:"
    echo "   NASA_API_KEY=your_actual_api_key_here"
    echo "   Get your API key from: https://api.nasa.gov"
else
    echo "✅ .env file already exists"
fi

echo ""
echo "✅ Setup complete!"
echo ""
echo "To activate the virtual environment in the future, run:"
echo "   source .venv/bin/activate"
echo ""
echo "To run the scraper:"
echo "   python nasa_neo_scraper.py"
echo "   or"
echo "   python run_scraper.py --test"
