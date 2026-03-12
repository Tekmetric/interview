COPY
(
    SELECT count(*) AS close_approaches_under_0_2_au
FROM '{source}'
WHERE miss_distance_au < 0.2
)
TO '{output}'
(FORMAT PARQUET)
