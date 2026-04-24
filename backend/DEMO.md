# Autoshop API: Summary

## What I built

A small web service that stores and manages "autoshops". The service lets you list shops, look up a single shop, add a new one, change the details of an existing one, and remove one.

Every request is validated (you can't create a shop with a blank name, for example), every response is consistent, and every error comes back in a predictable shape so other teams can build against it safely.

## The shop record

```json
{
  "id": 1,
  "name": "Hopper Motors",
  "address": "123 Main St, Austin, TX",
  "phone": "555-0100",
  "createdAt": "2026-04-24T18:00:00Z",
  "updatedAt": "2026-04-24T18:00:00Z"
}
```

`id` and the timestamps are filled in by the service. Callers only need to send `name`, `address`, and `phone`.

## What you can do with it

- **List** all shops (paginated, sortable)
- **Get** one shop by id
- **Create** a new shop
- **Replace** all fields of an existing shop
- **Delete** a shop

## Where things live (running locally)

- **Main list endpoint**: http://localhost:8080/api/autoshops
- **Interactive API docs (Swagger)**: http://localhost:8080/swagger-ui/index.html — click any operation, hit "Try it out", and send a real request

## How to demo using the `.http` file

Open **`backend/http/autoshop-api.http`** in IntelliJ. It's a pre-written walkthrough of 15 requests in the order a real user would make them: list the existing shops, create a new one, confirm it was saved, rename it, delete it, confirm it's gone. Both happy paths and expected error cases are included.

Each request has a green play icon next to it in the gutter. You can run them one at a time, or run the whole file.
