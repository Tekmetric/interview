from datetime import date, timedelta

import scrape_nasa
import clean_data
import analyze


if __name__ == '__main__':
    end_date = date.today()
    start_date = end_date - timedelta(days=7)

    data = scrape_nasa.fetch_data(start_date, end_date)
    clean_data.clean_data(data, end_date)
    analyze.analyze_data(data, end_date)


    print('all done!')