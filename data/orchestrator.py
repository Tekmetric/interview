import ingest
import flatter_and_enrich
import logging
from rich.logging import RichHandler

# Configure the basic logging with RichHandler
logging.basicConfig(
    level="INFO",
    format="%(message)s",
    handlers=[RichHandler(rich_tracebacks=True, show_level=True)]
)

log = logging.getLogger("rich")

def main():
    logging.basicConfig(level=logging.INFO)
    ingest_status = ingest.main()
    print(ingest_status)
    df, df_agg, base_faulty_records_cnt, major_faulty_records_cnt, total_02_approach = flatter_and_enrich.main()
    log.info(df.info)
    log.info(df_agg.info)
    log.info(f'Number of base faulty records: {base_faulty_records_cnt}')
    log.info(f'Number of major faulty records: {major_faulty_records_cnt}')
    log.info(f'totalc close approaches: {total_02_approach}')

    return f'Process finished Successfully'


if __name__ == "__main__":
    df = main()
