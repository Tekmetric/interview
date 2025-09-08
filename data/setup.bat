@echo off
REM Setup script for NASA NEO Data Scraper (Windows)

echo 🚀 Setting up NASA NEO Data Scraper...

REM Check if Python 3.12.6 is available
python3.12 --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Python 3.12.6 not found. Please install Python 3.12.6 first.
    echo    You can download it from: https://www.python.org/downloads/
    pause
    exit /b 1
)

REM Create virtual environment
echo 📦 Creating virtual environment with Python 3.12.6...
python3.12 -m venv .venv

REM Activate virtual environment
echo 🔧 Activating virtual environment...
call .venv\Scripts\activate.bat

REM Upgrade pip
echo ⬆️  Upgrading pip...
python -m pip install --upgrade pip

REM Install dependencies
echo 📚 Installing dependencies...
pip install -r requirements.txt

REM Check if .env file exists
if not exist ".env" (
    echo ⚠️  Creating .env file template...
    copy .env.example .env
    echo 📝 Please edit .env file and add your NASA API key:
    echo    NASA_API_KEY=your_actual_api_key_here
    echo    Get your API key from: https://api.nasa.gov
) else (
    echo ✅ .env file already exists
)

echo.
echo ✅ Setup complete!
echo.
echo To activate the virtual environment in the future, run:
echo    .venv\Scripts\activate.bat
echo.
echo To run the scraper:
echo    python nasa_neo_scraper.py
echo    or
echo    python run_scraper.py --test
pause
