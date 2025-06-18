from unittest.mock import MagicMock

import pytest
import torch

from src.similarity_lookup import SimilarityMatcher


@pytest.fixture
def mock_matcher():
    matcher = SimilarityMatcher()
    matcher.device = "cpu"
    matcher.similarity_threshold = 0.9
    matcher.return_unknown_if_no_match = True
    matcher.unknown_label = "unknown"

    # Fake label list and embeddings
    matcher.labels = ["Engine :: Camshaft", "Lighting :: Fog Lamp Bulb"]
    matcher.embeddings = torch.tensor(
        [
            [0.1, 0.2, 0.3],  # Embedding for label 1
            [0.4, 0.5, 0.6],  # Embedding for label 2
        ]
    )

    # Mock the model.encode call
    matcher.model = MagicMock()
    matcher.model.encode = MagicMock(return_value=torch.tensor([[0.0, 0.0, 1.0]]))

    return matcher


def test_predict_above_threshold(mock_matcher):
    # Use an input vector close to the 2nd label embedding
    mock_matcher.model.encode = MagicMock(return_value=torch.tensor([[0.4, 0.5, 0.6]]))
    mock_matcher.similarity_threshold = 0.5  # low enough to allow match
    result = mock_matcher.predict("test title")
    assert result == "Lighting :: Fog Lamp Bulb"


def test_predict_below_threshold_returns_unknown(mock_matcher):
    mock_matcher.similarity_threshold = 0.99  # force no match
    result = mock_matcher.predict("test title")
    assert result == "unknown"


def test_predict_below_threshold_returns_none_if_not_configured(mock_matcher):
    mock_matcher.similarity_threshold = 0.99
    mock_matcher.return_unknown_if_no_match = False
    result = mock_matcher.predict("test title")
    assert result is None
