### Background

You work at a company that processes repair orders from thousands of
independent auto repair shops across North America. Each day, repair technicians
submit work orders with varying levels of detail and technical expertise. Your team
needs to automatically classify these repairs to enable better inventory management,
pricing recommendations, and quality control.

### The Problem

Design and implement a production system that automatically classifies automotive repair
titles into predefined categories from a standardized parts taxonomy.

More specifically, your task is to build a multi-label classifier for titles of automotive repairs.

_Note_: _We know all too well that building such a classifier may take many many hours of work. We
don't  have high expectations from the accuracy of your solution but we do expect that you are prepared
to defend your choices._

#### Datasets Provided

`taxonomy.csv`

A standardized automotive parts taxonomy with two hierarchical levels:

- `section` - High-level assembly (e.g., "Engine", "Brakes", "Electrical"),
- `name`    - Specific part within the assembly (e.g., "Brake Pads", "Alternator", "Oil Filter")

_Examples_:
Brakes -> Disc Brake Caliper
Lighting -> Interior Bulb

`labeled_repairs.csv`

Historical repair data with three columns:

- title - Natural language title of the repair work. Example:  "Replacing bulb for front right turning light,"
- section - Ground truth section label from taxonomy. May be ‘unknown’ for repairs that don't have an analog in the taxonomy
- name - Ground truth part name from taxonomy. May be ‘unknown’ for repairs that don't have an analog in the taxonomy

Examples:

- Removing and installing/replacing front left or right brake caliper -> Brakes, Disc Brake Caliper
- Removing/installing/replacing brake lamp -> Lighting,Exterior Bulb
- Replacing rear left side panel -> unknown,unknown

### Requirements/Constraints

You need to design and implement a system that is able to take as input a
repair order title and output the predicted label from the `taxonomy.csv` or
special label `unknown` if no label could be inferred.

- The classifier is part of a production pipeline that processes up to 10 million items a day.
- It is usual that 40% of this traffic is with items that have previously passed through
the pipeline in the same day for various reason that are not related to our classifier.
- We estimate that an incorrect classification may cost the company $0.0005 because of later manual labeling.  
- Parts taxonomy updated quarterly with 5-7% new additions
- Repair titles formats might change from shop to shop
