# TekBoard

> Kanban board for managing auto repair orders. Full-stack TypeScript app.

**Stack**: React 19, TypeScript, TanStack Query, @dnd-kit, Tailwind, ShadCn, Express, SQLite

---

## Quick Start

```bash
pnpm install
pnpm run seed
pnpm run dev
```

Open [localhost:5173](http://localhost:5173)

---

## Features

- Dashboard with KPIs (WIP count, overdue orders, waiting parts)
- Clicking on KPIs, opens the kanban with the corresponding filter selected
- Kanban board with drag & drop status updates
- Search and filter by customer, vehicle, technician, status
- CRUD operations on repair orders (create, assign tech, update priority, add notes)
- Batch operations (multi-select cards, bulk update)

---

## Technical Details

### State Management

Using TanStack Query for server state

- Built-in caching and request deduplication
- Optimistic updates with automatic rollback

URL parameters for filter state (shareable links, browser history works).

### Validation

Same validation logic runs on client and server (shared TypeScript code)
Client-side validation prevents invalid API calls. Server-side validation enforces business rules.

### Backend Structure

```
server/
├── data/              # Database, schema, seed
├── domains/           # Business domains
│   ├── repairOrders/  # types, repository, routes
│   └── technicians/   # types, repository, routes
└── shared/            # Shared validation logic
```

Clean separation between data access, business logic, and HTTP routing.

## Testing

```bash
pnpm test              # All tests
pnpm run test:server   # Backend only
pnpm run test:coverage # Coverage report
```

Added tests for validation logic, for API endpoints.

---

## Code Principles

**Type Safety**

- TypeScript strict mode
- Zod schemas for runtime validation
- Shared types between client and server

**Functional Style**

- Pure functions where possible
- No classes (functional components + hooks)
- Early returns, small functions

**Separation of Concerns**

- Business logic: Pure functions (no framework dependencies)
- Data layer: Database operations
- API layer: HTTP routing
- UI layer: React components

---

## Documentation

- [PRODUCT_SPEC.md](./docs/PRODUCT_SPEC.md) - Features, user flows, data model
- [ARCHITECTURE.md](./docs/ARCHITECTURE.md) - System design, data flow, API patterns
- [TODO.md](./docs/TODO.md) - Implementation progress

---

## Assignment

Original goals:

1. Fetch data from backend CRUD API
2. Display data on Kanban board
3. Apply styling (Tailwind + shadcn/ui)
4. Have fun

---

Create PR to [Tekmetric/interview](https://github.com/Tekmetric/interview) when done.
