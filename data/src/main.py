"""
Main entry point for NASA NEO Data Pipeline.

This module provides a command-line interface for running the pipeline with
configurable options. It handles argument parsing, configuration loading,
pipeline execution, and result reporting.

Usage:
    python -m src.main [options]

Examples:
    # Run with default configuration (from environment)
    python -m src.main

    # Override max objects
    python -m src.main --max-objects 100

    # Override API base URL
    python -m src.main --api-url https://api.nasa.gov/neo/rest/v1

    # Override batch size for processing
    python -m src.main --batch-size 500

    # Override data path
    python -m src.main --data-path /path/to/data
"""

import sys
import argparse
import logging
from typing import Optional

from .config import PipelineConfig
from .pipeline import NEOPipeline
from .logging_config import setup_logging
from .exceptions import NEOPipelineError


def parse_arguments() -> argparse.Namespace:
    """
    Parse command-line arguments for pipeline configuration.
    
    Returns:
        argparse.Namespace: Parsed command-line arguments
    """
    parser = argparse.ArgumentParser(
        description='NASA Near Earth Object Data Pipeline',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Run with default configuration
  python -m src.main

  # Override max objects to fetch
  python -m src.main --max-objects 100

  # Override API base URL
  python -m src.main --api-url https://api.nasa.gov/neo/rest/v1

  # Override batch size
  python -m src.main --batch-size 500

  # Override data path
  python -m src.main --data-path /path/to/data

  # Enable debug logging
  python -m src.main --log-level DEBUG

Environment Variables:
  NASA_API_KEY        Required - NASA API key for authentication
  NASA_API_BASE_URL   Optional - Override default API base URL
  MAX_OBJECTS         Optional - Maximum number of objects to fetch
  BATCH_SIZE          Optional - Batch size for streaming writes
  BASE_DATA_PATH      Optional - Base path for data storage
        """
    )
    
    # API configuration
    parser.add_argument(
        '--api-key',
        type=str,
        help='NASA API key (overrides NASA_API_KEY environment variable)'
    )
    parser.add_argument(
        '--api-url',
        type=str,
        help='NASA API base URL (default: https://api.nasa.gov/neo/rest/v1)'
    )
    parser.add_argument(
        '--max-objects',
        type=int,
        help='Maximum number of NEO objects to fetch (default: 200)'
    )
    parser.add_argument(
        '--page-size',
        type=int,
        help='Number of objects per API page (default: 20)'
    )
    
    # Storage configuration
    parser.add_argument(
        '--data-path',
        type=str,
        help='Base path for data storage (default: interview/data)'
    )
    
    # Processing configuration
    parser.add_argument(
        '--batch-size',
        type=int,
        help='Batch size for streaming writes (default: 1000)'
    )
    
    # Aggregation configuration
    parser.add_argument(
        '--close-approach-threshold',
        type=float,
        help='Threshold in AU for close approaches (default: 0.2)'
    )
    
    # Logging configuration
    parser.add_argument(
        '--log-level',
        type=str,
        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
        default='INFO',
        help='Logging level (default: INFO)'
    )
    
    return parser.parse_args()


def load_configuration(args: argparse.Namespace) -> PipelineConfig:
    """
    Load pipeline configuration from environment and CLI arguments.
    
    Configuration is loaded in the following order (later overrides earlier):
    1. Default values from PipelineConfig
    2. Environment variables
    3. Command-line arguments
    
    Args:
        args: Parsed command-line arguments
        
    Returns:
        PipelineConfig: Configuration instance with merged settings
        
    Raises:
        ValueError: If required configuration (API key) is missing
    """
    # If API key provided via CLI, use it directly
    if args.api_key:
        import os
        os.environ['NASA_API_KEY'] = args.api_key
    
    # Start with environment-based configuration
    config = PipelineConfig.from_env()
    
    # Override with CLI arguments if provided (api_key already handled above)
    if args.api_url:
        config.api_base_url = args.api_url
    
    if args.max_objects is not None:
        config.max_objects = args.max_objects
    
    if args.page_size is not None:
        config.page_size = args.page_size
    
    if args.data_path:
        config.base_data_path = args.data_path
    
    if args.batch_size is not None:
        config.batch_size = args.batch_size
    
    if args.close_approach_threshold is not None:
        config.close_approach_threshold_au = args.close_approach_threshold
    
    return config


def print_summary(result) -> None:
    """
    Print a summary of pipeline execution results.
    
    Args:
        result: PipelineResult instance with execution details
    """
    print("\n" + "=" * 80)
    print("PIPELINE EXECUTION SUMMARY")
    print("=" * 80)
    print(f"\nData Processing:")
    print(f"  Raw records written:     {result.raw_records_count:,}")
    print(f"  Curated records written: {result.curated_records_count:,}")
    
    print(f"\nAggregations:")
    print(f"  Close approaches (< 0.2 AU): {result.close_approaches_count:,}")
    print(f"  Approaches by year:")
    for year in sorted(result.approaches_by_year.keys()):
        count = result.approaches_by_year[year]
        print(f"    {year}: {count:,}")
    
    print(f"\nOutput Paths:")
    print(f"  Raw layer:        {result.raw_path}")
    print(f"  Curated layer:    {result.curated_path}")
    print(f"  Aggregates layer: {result.aggregates_path}")
    
    print("\n" + "=" * 80)
    print("Pipeline completed successfully!")
    print("=" * 80 + "\n")


def main() -> int:
    """
    Main entry point for the pipeline CLI.
    
    This function:
    1. Parses command-line arguments
    2. Sets up logging
    3. Loads configuration from environment and CLI args
    4. Initializes and runs the pipeline
    5. Handles exceptions and exit codes
    6. Prints summary of results
    
    Returns:
        int: Exit code (0 for success, non-zero for failure)
    """
    # Parse arguments
    args = parse_arguments()
    
    # Setup logging
    setup_logging(level=args.log_level)
    logger = logging.getLogger(__name__)
    
    try:
        # Load configuration
        logger.info("Loading configuration...")
        config = load_configuration(args)
        
        # Log configuration (without sensitive data)
        logger.info("Configuration loaded:")
        logger.info("  API URL: %s", config.api_base_url)
        logger.info("  Max objects: %d", config.max_objects)
        logger.info("  Page size: %d", config.page_size)
        logger.info("  Data path: %s", config.base_data_path)
        logger.info("  Batch size: %d", config.batch_size)
        logger.info("  Close approach threshold: %.2f AU", config.close_approach_threshold_au)
        
        # Initialize pipeline
        logger.info("Initializing pipeline...")
        pipeline = NEOPipeline(config)
        
        # Run pipeline
        logger.info("Starting pipeline execution...")
        result = pipeline.run()
        
        # Print summary
        print_summary(result)
        
        return 0
        
    except ValueError as e:
        # Configuration errors (e.g., missing API key)
        logger.error("Configuration error: %s", str(e))
        print(f"\nError: {str(e)}", file=sys.stderr)
        print("\nPlease ensure NASA_API_KEY environment variable is set.", file=sys.stderr)
        print("You can obtain an API key from: https://api.nasa.gov/", file=sys.stderr)
        return 1
        
    except NEOPipelineError as e:
        # Pipeline-specific errors
        logger.error("Pipeline error: %s", str(e), exc_info=True)
        print(f"\nPipeline error: {str(e)}", file=sys.stderr)
        return 2
        
    except KeyboardInterrupt:
        # User interrupted execution
        logger.warning("Pipeline execution interrupted by user")
        print("\n\nPipeline execution interrupted by user.", file=sys.stderr)
        return 130  # Standard exit code for SIGINT
        
    except Exception as e:
        # Unexpected errors
        logger.critical("Unexpected error: %s", str(e), exc_info=True)
        print(f"\nUnexpected error: {str(e)}", file=sys.stderr)
        print("Please check the logs for more details.", file=sys.stderr)
        return 3


if __name__ == '__main__':
    sys.exit(main())
