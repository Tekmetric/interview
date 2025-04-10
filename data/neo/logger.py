import logging

# Configure a root logger
logger = logging.getLogger("neo_pipeline")
logger.setLevel(logging.DEBUG)

# Stream handler for console output
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)

# Formatter for logs
formatter = logging.Formatter("%(asctime)s - %(levelname)s - %(message)s")
ch.setFormatter(formatter)

# Add handler only once (avoid duplicates if reloaded)
if not logger.hasHandlers():
    logger.addHandler(ch)
