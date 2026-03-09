# Automotive repair shop domain

## Core Domain

### Customer

Customer that is paying to have their vehicle serviced.

### Vehicle

Vehicle that is being serviced.

### WorkOrder

A service order for a vehicle constituting a single job.

### PartLineItem

Line item for a part used in a work order.

### LaborLineItem

Line item for labor performed in a work order.

### Related domain but out of scope for demo

Shop
Payments
Invoices
Parts catalog
Employees
etc.

## Design choices

### Layered architecture
* api -> service -> persistence
* mapping between layers via MapStruct or manual mapping if needed.
* domain shared, pragmatic choice to avoid mapping value types between layers.

### UUID v7 for primary keys

* Solves pre-assignment and hash stability for equals/hashCode.
* Time bit ordering makes it sortable and index-friendly vs v4.
* Less compact than Int/Long for indexes but unlocks long-term scalability without needing to change the design.
  * I would also argue that if the difference in index performance matters, then a single db is probably not the best choice.

### Entity model
* PartLineItem and LaborLineItem are separate objects to allow better scaling and flexibility.
  * Intentionally not using MappedSuperclass.
  * Intentionally not exposing a Repository for these entities.
* Customer id FK on Vehicle and WorkOrder to allow independent lookup, flexible business logic (change in ownership)

### Domain & Api design
* Traditional REST API would expose vehicles and work orders on the customer controller.
  * Ex. /customers/{customerId}/vehicles/{vehicleId}/workOrders
  * This does not scale well if services are required to be split into distributed services.
* Instead, each resource gets its own top-level controller. Ex. /vehicles, /customers, /work-orders
  * Related resources are fetched via query parameter filters rather than nested routes.
  * Ex. /vehicles?customerId={id}, /work-orders?vehicleId={id}
  * This keeps controllers independent and makes it straightforward to split into separate services later if needed.
* Customer id 

### Thin domain and responses
* Responses return IDs for related resources rather than embedding full objects.
* When creating/updating a resource with a foreign key (ex. posting a vehicle with a customerId), use EntityManager.getReference() to set the reference without fetching.
  * No DB round trip. Hibernate gets a lazy proxy with just the ID.
  * The DB foreign key constraint handles validation. Translate constraint violations into clean error responses.