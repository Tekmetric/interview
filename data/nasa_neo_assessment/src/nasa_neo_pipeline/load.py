from __future__ import annotations

import shutil
from pathlib import Path

import pandas as pd


def reset_output_path(path: Path) -> None:
    if path.exists():
        shutil.rmtree(path)
    path.mkdir(parents=True, exist_ok=True)


def write_dataset(
    df: pd.DataFrame,
    base_path: Path,
    layer: str,
    dataset: str,
    snapshot_date: str,
    filename: str,
) -> None:
    path = Path(base_path) / layer / dataset / snapshot_date
    reset_output_path(path)
    df.to_parquet(path / filename, index=False)


# Gold is stored as a single-row parquet file with both required aggregations.
def write_gold_aggregations(
    total_close_approaches_lt_0_2_au: int,
    approaches_by_year: dict[str, int],
    base_path: Path,
    snapshot_date: str,
) -> None:
    import pyarrow as pa
    import pyarrow.parquet as pq

    path = Path(base_path) / "gold" / "aggregations" / snapshot_date
    reset_output_path(path)

    total_array = pa.array([int(total_close_approaches_lt_0_2_au)], type=pa.int64())
    approaches_map_array = pa.array(
        [list(approaches_by_year.items())],
        type=pa.map_(pa.string(), pa.int64()),
    )

    table = pa.Table.from_arrays(
        [total_array, approaches_map_array],
        names=[
            "total_close_approaches_lt_0_2_au",
            "approaches_by_year",
        ],
    )

    pq.write_table(table, path / "aggregations.parquet")
