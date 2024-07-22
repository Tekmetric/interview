# Python Coding Exercise

Your task is to build a python script to gather data from the US Government's vehicle recall data API, and save that data into S3.

The page for the API is here: https://catalog.data.gov/dataset/recalls-data

If you want to use local-stack to save data into a “local S3” that’s ok, otherwise pseudocode to represent the S3 upload is fine. Also acceptable is writing out files to the local filesystem in whatever directory structure you would use for S3.

### Requirements
- Create an account at api.data.gov to get an API key: https://api.data.gov/signup/
- Retrieve the API data in the script using your new API key
    - Instructions on API key usage here: https://api.data.gov/docs/developer-manual/
- Data should be saved in Parquet format
- Recalls should be stored with 1 file for each year, named {year}.parquet
    - We want to save the following columns in our files in S3:
        - Id
        - created_at
        - updated_at
        - report_received_date
        - nhtsa_id
        - recall_link (stored as a string of just the link)
        - manufacturer
        - subject
        - component
        - mfr_campaign_number
        - recall_type
        - potentially_affected
        - defect_summary
        - consequence_summary
        - corrective_action
        - fire_risk_when_parked
        - do_not_drive
        - completion_rate
- Store the following aggregations:
    - Number of recalls per manufacturer per year
    - Number of recalls per component per year
    - Number of recalls per type per manufacturer

### Submitting your coding exercise
Once you have finished your script, please create a PR into Tekmetric/interview
