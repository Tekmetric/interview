import argparse
import random
import string
from pathlib import Path

makes_models = {
    "Honda": ["Civic", "Accord", "Fit", "CR-V"],
    "Toyota": ["Corolla", "Camry", "RAV4", "Prius"],
    "Ford": ["Focus", "Fusion", "F-150", "Escape"],
    "Chevrolet": ["Malibu", "Cruze", "Tahoe", "Equinox"],
    "Nissan": ["Altima", "Sentra", "Rogue", "Leaf"]
}

owners = ["Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hank"]


def random_vin():
    chars = string.ascii_uppercase + string.digits
    return ''.join(random.choices(chars, k=17))


def random_license_plate():
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=7))


def generate_data_sql(num_vehicles=100, filename='data.sql'):
    file_path = Path(filename)
    file_path.parent.mkdir(parents=True, exist_ok=True)
    file_path.touch(exist_ok=True)

    with open(filename, 'w') as f:
        for i in range(1, num_vehicles + 1):
            make = random.choice(list(makes_models.keys()))
            model = random.choice(makes_models[make])
            vin = random_vin()
            year = random.randint(1990, 2025)
            license_plate = random_license_plate()
            owner = random.choice(owners)

            f.write(
                f"INSERT INTO VEHICLES (vin, make, model, manufacture_year, license_plate, owner_name, created_by) "
                f"VALUES ('{vin}', '{make}', '{model}', {year}, '{license_plate}', '{owner}', 'system');\n"
            )
    print(f"{num_vehicles} vehicles written to {filename}.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Generate data.sql for VEHICLES table.")
    parser.add_argument("-n", "--num", type=int, default=100, help="Number of vehicles to generate (default 100)")
    parser.add_argument("-f", "--file", type=str, default="data.sql",
                        help="Output SQL file path")
    args = parser.parse_args()

    generate_data_sql(args.num, args.file)
