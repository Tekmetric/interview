#!/usr/bin/env python3
"""
Test runner script for NASA NEO Data Scraper
Provides convenient test execution with different configurations.
"""

import subprocess
import sys
import argparse
from pathlib import Path


def run_command(cmd, description):
    """Run a command and handle errors."""
    print(f"\n{'='*50}")
    print(f"Running: {description}")
    print(f"Command: {' '.join(cmd)}")
    print(f"{'='*50}")
    
    result = subprocess.run(cmd, capture_output=True, text=True)
    
    if result.stdout:
        print("STDOUT:")
        print(result.stdout)
    
    if result.stderr:
        print("STDERR:")
        print(result.stderr)
    
    if result.returncode != 0:
        print(f"❌ {description} failed with return code {result.returncode}")
        return False
    else:
        print(f"✅ {description} completed successfully")
        return True


def main():
    parser = argparse.ArgumentParser(description='NASA NEO Data Scraper Test Runner')
    parser.add_argument(
        '--unit', 
        action='store_true', 
        help='Run only unit tests'
    )
    parser.add_argument(
        '--integration', 
        action='store_true', 
        help='Run only integration tests'
    )
    parser.add_argument(
        '--coverage', 
        action='store_true', 
        help='Generate coverage report'
    )
    parser.add_argument(
        '--verbose', 
        action='store_true', 
        help='Verbose output'
    )
    parser.add_argument(
        '--file', 
        type=str, 
        help='Run specific test file'
    )
    
    args = parser.parse_args()
    
    # Base pytest command
    cmd = ['python', '-m', 'pytest']
    
    if args.verbose:
        cmd.append('-v')
    
    if args.coverage:
        cmd.extend(['--cov=utils', '--cov=nasa_neo_scraper', '--cov-report=term-missing'])
    
    if args.unit:
        cmd.extend(['-m', 'unit'])
    elif args.integration:
        cmd.extend(['-m', 'integration'])
    
    if args.file:
        cmd.append(f'tests/{args.file}')
    else:
        cmd.append('tests/')
    
    # Run the tests
    success = run_command(cmd, "Test Suite")
    
    if args.coverage and success:
        print(f"\n📊 Coverage report generated in htmlcov/index.html")
    
    if not success:
        sys.exit(1)
    
    print(f"\n🎉 All tests completed successfully!")


if __name__ == "__main__":
    main()
