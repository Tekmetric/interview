# Java Spring Boot API Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven
- Java 21

#### After finishing the goals listed below create a PR

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

### Description

The goal is to develop a simple Auto Repair Management System that manages vehicle repairs, tracks service orders, 
and generates estimates and invoices in PDF format

### Domain

- RepairOrder: service request for a vehicle submitted to an auto repair shop (id, vin, created_at, updated_at, is_deleted, serviceItems, estimate_pdf)
- ServiceItem: Individual repair or maintenance tasks requested or performed(id, name, description, quantity, price, repair_order_id, created_at, updated_at, is_deleted)

### API
Repair Order Management:
CREATE, READ, UPDATE, DELETE

Service Item Management
CREATE, READ, UPDATE, DELETE of a service item which is part of the repair order
