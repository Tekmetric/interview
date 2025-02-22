### Documentation on the structure of the Scraping service.

# Requirements
- Create an account at [api.nasa.gov](https://api.nasa.gov) to get an API key
- Find the docs for the Near Earth Object Web Service (below the signup on the same page)
- Data should be saved in Parquet format
- Design the code such that the scraping and processing part could easily be scaled up GBs of data by swapping in and out various implementations.
- Use the Browse API to request data
    - There are over 1800 pages of near Earth objects, so we'll limit ourselves to gathering the first 200 near earth objects
- We want to save the following columns in our file(s):
    - id
    - neo_reference_id
    - name
    - name_limited
    - designation
    - nasa_jpl_url
    - absolute_magnitude_h
    - is_potentially_hazardous_asteroid
    - minimum estimated diameter in meters
    - maximum estimated diameter in meters
    - **closest** approach miss distance in kilometers
    - **closest** approach date
    - **closest** approach relative velocity in kilometers per second
    - first observation date
    - last observation date
    - observations used
    - orbital period
- Store the following aggregations:
    - The total number of times our 200 near earth objects approached closer than 0.2 astronomical units (found as miss_distance.astronomical)
    - The number of close approaches recorded in each year present in the data


# Application components
In order to support the above requirements, some specific application components should be created.

1. Handling HTTP requests
This should be done via a RequestHandler component. The NEO Service API is a REST API and using the browse API call we get paged results. The
RequestHandler component will only request one page at a time and return the result. TODO - async handling of requests may be an option, in order
to scale further. Look into async HTTP request handling.

2. The resulting JSON response containing the browse request data needs to be processed in order to save some information about the asteroids and
to do some aggregations as well. There should be a component for this, called ResponseProcessor
