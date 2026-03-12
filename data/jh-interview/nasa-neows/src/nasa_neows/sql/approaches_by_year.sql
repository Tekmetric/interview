COPY
(
    SELECT
    close_approach_date[:4]
AS year,
        count
(*) AS close_approach_count
    FROM '{source}'
    GROUP BY year
    ORDER BY year
) TO '{output}'
(FORMAT PARQUET)
