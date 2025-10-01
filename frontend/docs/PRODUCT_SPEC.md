# TekBoard – Product Specification

## Overview

A Kanban board for auto repair shops to visualize and manage Repair Orders (ROs) through drag-and-drop workflow management.

**Demo Focus**: Simple, polished app with mocked auth and ~50-150 ROs.

---

## Core Features

### 1. Welcome Screen (Mocked Auth)

- Clean welcome page with brand
- "Continue as Demo User" button
- Mock social buttons (Google/Apple)
- Stores demo user in localStorage → routes to Dashboard

### 2. Dashboard (Quick Overview)

**KPI Cards:**

- Total WIP (work in progress count)
- Overdue ROs (promisedAt < now)
- Waiting Parts (count)
- Awaiting Approval (count)

**Quick Lists:**

- Top 5 Overdue ROs (RO#, customer, promised time, status)
- Top 5 Recent/Today ROs

**Actions:**

- "Open Kanban Board" (primary button)
- "Create RO" (opens drawer)

### 3. Kanban Board (Primary View)

**5 Columns:**

- NEW
- AWAITING_APPROVAL
- IN_PROGRESS
- WAITING_PARTS
- COMPLETED

**Card Shows:**

- RO number (#1001)
- Customer name
- Vehicle (2018 Honda Accord)
- License plate (ABC123)
- Promised time (highlight if overdue)
- Technician avatar/initials
- Priority/tag chips

**Features:**

- Drag & drop to change status
- Optimistic updates (instant UI, rollback on error)
- Search by customer/plate/RO#
- Filter by technician
- Sort by promised time
- URL-based filters (shareable)

### 4. RO Details Drawer

Opens when clicking a card.

**Edit Fields:**

- Status (dropdown)
- Technician (dropdown)
- Priority (normal/high)
- Notes (textarea)
- Tags (chips)
- Promised time (datetime)

**Actions:**

- Save (PATCH with optimistic update)
- Delete (with confirmation dialog)
- Cancel (discard changes)

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

## Demo Script (5-Minute Interview)

1. **Welcome** → Click "Continue as Demo User"
2. **Dashboard** → Show KPIs, click "5 Overdue" to open filtered Board
3. **Drag & Drop** → Move RO from "Awaiting Approval" → "In Progress"
   - Show optimistic update + success toast
4. **Edit RO** → Click card, assign technician, add note, save
5. **Search** → Type license plate, show filtering
6. **Delete RO** → Click card, delete with confirmation (full CRUD demo)
7. **Highlight Features:**
   - Overdue highlighting
   - Column counts
   - Smooth animations
   - Optimistic updates throughout

---

## Technical Highlights

**UX States:**

- Loading: Column/card skeletons
- Empty: Friendly illustrations + "Create RO" prompt
- Errors: Toast notifications with retry
- Accessibility: Focus trap, ARIA live regions, keyboard nav

**Performance:**

- Handles 50-150 cards smoothly
- Memoized cards to avoid re-renders
- Optimistic updates for snappy UX

**Tech Stack:**

- React + TypeScript
- TanStack Query (data fetching)
- Drag-and-drop library (dnd-kit)
- Tailwind CSS + ShadCN components
- Vite for dev/build
- Vitest + jsdom for testing

**Testing Strategy:**

- **Unit Tests:** Core business logic functions (status transition validation, date calculations, filtering logic)
- **Component Tests:** Interactive UI components (RO card, drawer, filters) with Vitest + Testing Library
- **Integration Tests:** API endpoints with mocked database responses
- **Test Location:** Co-locate tests with source (`__tests__/` folders or `*.test.ts(x)`)
- **Coverage Focus:** Business-critical paths (status transitions, overdue calculations, drag-and-drop state management)
- **Commands:**
  - `pnpm test` – run tests in watch mode
  - `pnpm test:coverage` – generate coverage report
  - `pnpm test:watch` – continuous test runner

**Test Examples:**

```typescript
// Business logic test
describe('validateStatusTransition', () => {
  it('allows NEW → IN_PROGRESS', () => {
    expect(canTransition('NEW', 'IN_PROGRESS')).toBe(true)
  })

  it('rejects WAITING_PARTS → COMPLETED', () => {
    expect(canTransition('WAITING_PARTS', 'COMPLETED')).toBe(false)
  })
})

// Component test
describe('ROCard', () => {
  it('highlights overdue orders', () => {
    const overdueRO = { promisedAt: '2025-01-01T10:00:00Z', ... }
    render(<ROCard ro={overdueRO} />)
    expect(screen.getByText(/overdue/i)).toBeVisible()
  })
})

// Delete flow test
describe('RODetailsDrawer', () => {
  it('shows confirmation before deleting', async () => {
    const onDelete = vi.fn()
    render(<RODetailsDrawer ro={mockRO} onDelete={onDelete} />)

    await userEvent.click(screen.getByRole('button', { name: /delete/i }))
    expect(screen.getByText(/confirm/i)).toBeVisible()

    await userEvent.click(screen.getByRole('button', { name: /confirm/i }))
    expect(onDelete).toHaveBeenCalledWith(mockRO.id)
  })
})
```

---

## Out of Scope (MVP)

- Real authentication/OAuth
- Payments or parts integrations
- Full table/list view
- Complex scheduling
- WIP limits on columns (stretch goal)
- Soft delete or recovery mechanism (hard delete only)
