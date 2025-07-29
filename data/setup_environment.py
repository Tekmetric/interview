#!/usr/bin/env python3
"""
Environment setup script for NASA NEO Data Processor

This script helps set up the Python virtual environment and install dependencies
"""

import os
import sys
import subprocess
import platform
from pathlib import Path


def run_command(command, check=True):
    """Run a shell command and return the result"""
    try:
        result = subprocess.run(
            command, 
            shell=True, 
            check=check, 
            capture_output=True, 
            text=True
        )
        return result
    except subprocess.CalledProcessError as e:
        print(f"❌ Command failed: {command}")
        print(f"Error: {e.stderr}")
        if check:
            sys.exit(1)
        return e


def check_python_version():
    """Check if Python version is 3.8 or higher"""
    version = sys.version_info
    if version.major < 3 or (version.major == 3 and version.minor < 8):
        print(f"❌ Python 3.8+ required. Current version: {version.major}.{version.minor}")
        sys.exit(1)
    print(f"✅ Python version: {version.major}.{version.minor}.{version.micro}")


def check_java():
    """Check if Java is installed (required for PySpark)"""
    try:
        result = run_command("java -version", check=False)
        if result.returncode == 0:
            print("✅ Java is installed")
            return True
        else:
            print("⚠️  Java not found. PySpark requires Java 8 or higher.")
            print("   Please install Java from: https://adoptopenjdk.net/")
            return False
    except Exception:
        print("⚠️  Could not check Java installation")
        return False


def create_virtual_environment():
    """Create Python virtual environment"""
    venv_path = Path("venv")
    
    if venv_path.exists():
        print("✅ Virtual environment already exists")
        return
    
    print("📦 Creating virtual environment...")
    run_command(f"{sys.executable} -m venv venv")
    print("✅ Virtual environment created")


def get_pip_command():
    """Get the correct pip command for the current platform"""
    if platform.system() == "Windows":
        return "venv\\Scripts\\pip"
    else:
        return "venv/bin/pip"


def install_dependencies():
    """Install Python dependencies"""
    print("📦 Installing Python dependencies...")
    
    pip_cmd = get_pip_command()
    
    # Upgrade pip first
    run_command(f"{pip_cmd} install --upgrade pip")
    
    # Install dependencies
    run_command(f"{pip_cmd} install -r requirements.txt")
    
    print("✅ Dependencies installed successfully")


def create_env_file():
    """Create .env file from .env.example if it doesn't exist"""
    env_file = Path(".env")
    env_example = Path(".env.example")
    
    if env_file.exists():
        print("✅ .env file already exists")
        return
    
    if env_example.exists():
        print("📝 Creating .env file from .env.example...")
        with open(env_example, 'r') as src, open(env_file, 'w') as dst:
            dst.write(src.read())
        print("✅ .env file created")
        print("⚠️  Please edit .env and add your NASA API key")
    else:
        print("⚠️  .env.example not found")


def show_next_steps():
    """Show next steps to the user"""
    print("\n" + "="*60)
    print("🎉 Environment setup completed!")
    print("="*60)
    print("\nNext steps:")
    print("1. 📝 Edit .env file and add your NASA API key")
    print("   - Get your key at: https://api.nasa.gov")
    print("   - Replace 'your_nasa_api_key_here' with your actual key")
    print()
    print("2. 🚀 Activate the virtual environment:")
    
    if platform.system() == "Windows":
        print("   > venv\\Scripts\\activate")
    else:
        print("   $ source venv/bin/activate")
    
    print()
    print("3. ▶️  Run the NEO data processor:")
    print("   $ python neo_data_processor.py")
    print()
    print("4. 📊 Check the output in the 'data' directory")
    print()
    print("For help, see README.md")


def main():
    """Main setup function"""
    print("🛠️  Setting up NASA NEO Data Processor environment...")
    print()
    
    # Check requirements
    check_python_version()
    java_available = check_java()
    
    if not java_available:
        print("\n⚠️  Warning: Java not found. PySpark requires Java 8+")
        response = input("Continue anyway? (y/N): ").strip().lower()
        if response not in ['y', 'yes']:
            print("Setup cancelled. Please install Java and try again.")
            sys.exit(1)
    
    # Setup environment
    create_virtual_environment()
    install_dependencies()
    create_env_file()
    
    show_next_steps()


if __name__ == "__main__":
    main() 