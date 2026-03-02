"""Streamlit dashboard for exploring NASA Near Earth Object pipeline output."""

import streamlit as st
import pyarrow.parquet as pq

from nasa_neows.config import AGGREGATIONS_DIR, PROCESSED_DIR

st.set_page_config(page_title="NASA Near Earth Objects", layout="wide")
st.title("NASA Near Earth Objects")


def _read_parquet(path):
    """Read a Parquet file and return a pandas DataFrame."""
    if not path.exists():
        st.error(
            f"File not found: {path}. Run the pipeline first with `uv run nasa-neows`."
        )
        st.stop()
    return pq.read_table(path).to_pandas()


# Close approach metric
close_df = _read_parquet(AGGREGATIONS_DIR / "close_approaches_under_0_2_au.parquet")
st.metric(
    "Close Approaches Under 0.2 AU", close_df["close_approaches_under_0_2_au"].iloc[0]
)

# Approaches by year bar chart
st.subheader("Close Approaches by Year")
by_year_df = _read_parquet(AGGREGATIONS_DIR / "approaches_by_year.parquet")
by_year_df = by_year_df.set_index("year")
st.bar_chart(by_year_df["close_approach_count"])

# NEO explorer table
st.subheader("NEO Explorer")
neos_df = _read_parquet(PROCESSED_DIR / "neos.parquet")
st.dataframe(neos_df, width="stretch")
