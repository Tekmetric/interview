import pandas as pd


def analyze_data(data, today):
    approaches_per_year = {}
    very_close_approaches = 0

    for element in data:
        for approach in element['close_approach_data']:
            if 'miss_distance' in approach.keys() and float(approach['miss_distance']['astronomical']) > 0.2:
                very_close_approaches += 1
            if 'close_approach_date' in approach.keys():
                approach_year = approach['close_approach_date'][0:4]
                if approach_year in approaches_per_year.keys():
                    approaches_per_year[approach_year] += 1
                else:
                    approaches_per_year[approach_year] = 1


    # export approaches per year:
    approaches_list = []
    for year, cnt in approaches_per_year.items():
        approaches_list.append([year, cnt])
    df1 = pd.DataFrame(approaches_list, columns=['year', 'count_approaches'])
    df1.to_parquet(f'./neo/{today}/aggs/approaches_per_year.parquet.gzip', compression='gzip')
#    df1.to_csv(f'./neo/{today}/data/approaches_per_year.csv', index=False)

    # export total count of very close approaches:
    df2 = pd.DataFrame([[very_close_approaches]], columns = ['total_approaches_under_0.2_au'])
#    df2.to_csv(f'./neo/{today}/data/count_very_close_approaches.csv', index=False)
    df2.to_parquet(f'./neo/{today}/aggs/count_very_close_approaches.parquet.gzip', compression='gzip')

    print('done aggregating data')
