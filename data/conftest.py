import sys
from unittest.mock import MagicMock

sys.modules['yaml'] = MagicMock()
sys.modules['pyarrow'] = MagicMock()
sys.modules['pyarrow.parquet'] = MagicMock()
sys.modules['requests'] = MagicMock()
