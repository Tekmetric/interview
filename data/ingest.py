import requests
import json
import os
import config

# NASA NEO API endpoint
api_url = config.api_url
api_key = config.api_key

# enforce limits
target_results = config.target_results
results_per_page = config.results_per_page
pages_to_fetch = target_results // results_per_page

# setup
all_neos = []
landing_folder = config.landing_folder


def main():
    print(f"Fetching first {target_results} results from NASA NEO API...")
    print("=" * 60)

    for page in range(pages_to_fetch):
        params = {
            "api_key": api_key,
            "page": page
        }

        try:
            response = requests.get(api_url, params=params)
            response.raise_for_status()

            page_data = response.json()
            page_neos = page_data.get('near_earth_objects', [])
            all_neos.extend(page_neos)

            # Save each page to a separate file
            filename = os.path.join(landing_folder, f"page_{page}.json")
            with open(filename, 'w', encoding='utf-8') as f:
                json.dump(page_data, f, indent=2, ensure_ascii=False)

        except requests.exceptions.RequestException as e:
            print(f"Error fetching page {page}: {e}")
            break
        except Exception as e:
            print(f"Unexpected error on page {page}: {e}")
            break

    return f"✓ Successfully fetched {len(all_neos)} NEOs"


if __name__ == "__main__":
    df = main()
