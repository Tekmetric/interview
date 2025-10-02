# Product Spec

[README](../README.md) | [Architecture](./ARCHITECTURE.md) | [TODO](./TODO.md)

## Overview

Kanban board for managing auto repair orders.

Mocked auth, 50 seeded ROs for demo.

---

## Features

### Welcome Screen

- "Continue as Demo User" button
- Mock social login buttons
- Stores user in localStorage

### Dashboard

**KPI Cards**

- Total WIP count
- Overdue orders (promisedAt < now)
- Waiting Parts count
- Awaiting Approval count

**Quick Lists**

- Top 5 Overdue ROs
- Top 5 Recent ROs

**Actions**

- Open Kanban Board
- Create RO

### Kanban Board

**5 Columns**

- NEW
- AWAITING_APPROVAL
- IN_PROGRESS
- WAITING_PARTS
- COMPLETED

**Card Info**

- RO number
- Customer name
- Vehicle info
- License plate
- Promised time (highlighted if overdue)
- Technician avatar
- Priority chips

**Features**

- Drag & drop status changes
- Optimistic updates (instant UI, rollback on error)
- Search by customer/plate/RO#
- Filter by technician, status
- Sort by promised time
- URL-based filters (shareable)

### RO Details Drawer

Opens when clicking a card.

**Edit Fields**

- Status dropdown
- Technician dropdown
- Priority (normal/high)
- Notes textarea
- Tags
- Promised time

**Actions**

- Save (PATCH with optimistic update)
- Delete (with confirmation)
- Cancel

---

## Main User Flows

### Flow 1: Welcome → Dashboard → Board

```
Welcome Screen
  ↓ [Continue as Demo User]
Dashboard (see KPIs + quick lists)
  ↓ [Open Kanban Board]
Kanban Board (manage ROs)
```

### Flow 2: Update RO Status

```
Kanban Board
  ↓ [Drag card to new column]
Optimistic UI update
  ↓ Backend confirms/rejects
Toast notification + rollback if error
```

### Flow 3: Edit RO Details

```
Kanban Board
  ↓ [Click card]
Details Drawer opens
  ↓ [Edit fields, assign tech, add notes]
  ↓ [Save]
Optimistic update + toast
```

### Flow 4: Search & Filter

```
Kanban Board
  ↓ [Type in search: "ABC123"]
Board filters to matching ROs
  ↓ [Select technician filter]
Board shows only ROs for that tech
```

### Flow 5: Delete RO

```
Kanban Board
  ↓ [Click card]
Details Drawer opens
  ↓ [Click Delete button]
Confirmation dialog appears
  ↓ [Confirm deletion]
Optimistic removal from board
  ↓ Backend confirms
Toast notification + drawer closes
```

---

## Data Model

### Repair Order

Represents a customer's vehicle repair job tracked through the shop workflow.

**Identity:**

- Unique identifier (system-generated)
- Human-readable RO number (e.g. "1001")

**Customer & Vehicle:**

- Customer name (required, text)
- Vehicle year/make/model (required, text format: "2018 Honda Accord")
- License plate (required, text, searchable)

**Workflow:**

- Status (required, one of: NEW, AWAITING_APPROVAL, IN_PROGRESS, WAITING_PARTS, COMPLETED)
- Promised completion datetime (required, ISO 8601 format)
- Priority level (NORMAL or HIGH, defaults to NORMAL)
- Tags (optional list of keywords: "walk-in", "warranty", etc.)

**Assignment:**

- Assigned technician (optional reference to Technician entity)

**Notes:**

- Free-text notes field (optional, multi-line)

**Business Rules:**

- RO number must be unique across all orders
- Promised time used to calculate overdue status (current time > promised time)
- Status transitions are restricted (see Status Workflow below)
- Deleting an RO is permanent (no soft delete or recovery in MVP)

---

### Technician

Represents a shop technician who can be assigned to repair orders.

**Identity:**

- Unique identifier (system-generated)
- Full name (required, text)

**Profile:**

- Avatar image URL (optional)
- Skill tags (list of specialties: "engine", "transmission", "electrical", etc.)

**Business Rules:**

- A technician can be assigned to multiple ROs simultaneously
- Deleting/deactivating technicians not supported in MVP

---

### Status Workflow

Defines valid state transitions for Repair Orders. Invalid transitions are rejected by the API.

**Status Definitions:**

- **NEW** – Order created, not yet started
- **AWAITING_APPROVAL** – Waiting for customer approval (estimate sent)
- **IN_PROGRESS** – Technician actively working
- **WAITING_PARTS** – Work paused, waiting for parts delivery
- **COMPLETED** – Work finished, ready for customer pickup

**Allowed Transitions:**

| From Status       | Can Move To                                 |
| ----------------- | ------------------------------------------- |
| NEW               | AWAITING_APPROVAL, IN_PROGRESS              |
| AWAITING_APPROVAL | IN_PROGRESS, NEW                            |
| IN_PROGRESS       | WAITING_PARTS, COMPLETED, AWAITING_APPROVAL |
| WAITING_PARTS     | IN_PROGRESS                                 |
| COMPLETED         | _(terminal state, no transitions)_          |

**Business Rules:**

- Cannot skip states (e.g., NEW → COMPLETED requires going through IN_PROGRESS)
- COMPLETED is a terminal state (no way to reopen orders in MVP)
- Invalid transitions return error with allowed states for current status

---

## API Endpoints

**Base:** `/api`

- `GET /repairOrders` – list with filters (`q`, `status`, `technicianId`, `sort`)
- `POST /repairOrders` – create new RO
- `PATCH /repairOrders/:id` – update status, tech, notes, priority, etc.
- `DELETE /repairOrders/:id` – permanently delete RO (requires confirmation)
- `GET /technicians` – list for assignment

**Error Example:**

```json
{
  "error": "INVALID_TRANSITION",
  "message": "Cannot move from WAITING_PARTS to NEW",
  "allowed": ["IN_PROGRESS"]
}
```

---

## Demo Walkthrough

1. Welcome screen → Click "Continue as Demo User"
2. Dashboard → View KPIs, click "5 Overdue" to open filtered board
3. Drag card from "Awaiting Approval" → "In Progress" (optimistic update)
4. Try dragging COMPLETED card (validation prevents it)
5. Click card → Edit in drawer (assign tech, add note, save)
6. Search by license plate
7. Multi-select cards → Bulk update
8. Create new RO

---

## Testing

**Strategy**

- Unit tests: Business logic (status transitions, validation)
- Component tests: UI components (Vitest + Testing Library)
- Integration tests: API endpoints

**Test Examples**

```typescript
describe('validateStatusTransition', () => {
  it('allows NEW → IN_PROGRESS', () => {
    expect(canTransition('NEW', 'IN_PROGRESS')).toBe(true)
  })

  it('rejects WAITING_PARTS → COMPLETED', () => {
    expect(canTransition('WAITING_PARTS', 'COMPLETED')).toBe(false)
  })
})
```

**Commands**

- `pnpm test` – watch mode
- `pnpm test:coverage` – coverage report

---

## Out of Scope

**Excluded for time/scope:**

- Real authentication (mocked for demo)
- Parts ordering integrations
- Table/list view
- Scheduling/calendar view
- WIP limits
- Soft delete
- Real-time collaboration (WebSocket)

**Would add next:**

- Real auth (Clerk/Auth0)
- Advanced filtering (date ranges)
- Reporting dashboard
- Print/export
- Activity log
- Notifications
