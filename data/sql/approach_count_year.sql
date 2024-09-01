SELECT 
    approach_year, COUNT(*) as close_approach_count 
FROM view 
GROUP BY approach_year 