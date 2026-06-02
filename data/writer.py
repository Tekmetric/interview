from __future__ import annotations

import queue
import threading
from pathlib import Path

import pyarrow as pa
import pyarrow.parquet as pq

_SENTINEL = object()


class Writer(threading.Thread):
    """Dedicated writer thread that drains a queue of Arrow tables and writes a Parquet file.

    Buffers all incoming tables until a sentinel is received, then writes a single
    part file.  The caller controls batch sizing — one Writer per batch, one file per Writer.

    The Writer writes directly to ``output_dir`` with no opinion about partitioning
    or directory layout — the caller controls where files land.

    At scale, each Writer instance can map 1:1 to an independent file partition.
    Multiple Writer instances (one per batch) can run in parallel without coordination.
    """

    def __init__(
        self,
        q: queue.Queue,
        output_dir: Path,
        file_idx: int = 0,
    ) -> None:
        super().__init__(daemon=True)
        self._q = q
        self._output_dir = output_dir
        self._file_idx = file_idx
        self._buffer: list[pa.Table] = []
        self.total_records_written = 0

    def run(self) -> None:
        while True:
            page_table = self._q.get()
            if page_table is _SENTINEL:
                self._flush()
                break
            self._buffer.append(page_table)

    def _flush(self) -> None:
        if not self._buffer:
            return
        combined = pa.concat_tables(self._buffer)
        self._output_dir.mkdir(parents=True, exist_ok=True)
        path = self._output_dir / f"part-{self._file_idx:04d}.parquet"
        pq.write_table(combined, path)
        self.total_records_written += len(combined)
        self._buffer = []


def make_sentinel() -> object:
    """Return the sentinel value that signals the writer to stop."""
    return _SENTINEL
