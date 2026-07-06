landing_folder = "landing"
foundation_folder = 'foundation'
core_folder = 'core'
faulty_records_folder = "faulty_records"

api_url = "https://api.nasa.gov/neo/rest/v1/neo/browse"
api_key = "O8bcHuo9CllfBYNXf9Ube4ffzHANcddgk2j6tn3e"          # should never be public but in secrets manager

# We need 200 results, and each page has 20 results
# So we need to fetch 10 pages (pages 0-9)
target_results = 200
results_per_page = 20