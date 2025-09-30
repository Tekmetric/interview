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

---

## Data Model

### RepairOrder

```json
{
  "id": "ro-1001",
  "roNumber": "1001",
  "status": "NEW",
  "customerName": "Alex Perez",
  "vehicleYMM": "2018 Honda Accord",
  "plate": "ABC123",
  "promisedAt": "2025-10-01T20:00:00Z",
  "technicianId": "t-1",
  "priority": "NORMAL",
  "tags": ["walk-in"],
  "notes": ""
}
```

### Technician

```json
{
  "id": "t-1",
  "name": "Sam Chen",
  "avatar": "",
  "skills": ["engine"]
}
```

### Status Transitions (Enforced)

```json
{
  "NEW": ["AWAITING_APPROVAL", "IN_PROGRESS"],
  "AWAITING_APPROVAL": ["IN_PROGRESS", "NEW"],
  "IN_PROGRESS": ["WAITING_PARTS", "COMPLETED", "AWAITING_APPROVAL"],
  "WAITING_PARTS": ["IN_PROGRESS"],
  "COMPLETED": []
}
```

---

## API Endpoints

**Base:** `/api`

- `GET /repairOrders` – list with filters (`q`, `status`, `technicianId`, `sort`)
- `POST /repairOrders` – create new RO
- `PATCH /repairOrders/:id` – update status, tech, notes, priority, etc.
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
6. **Highlight Features:**
   - Overdue highlighting
   - Column counts
   - Smooth animations

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
- Drag-and-drop library (dnd-kit recommended)
- Tailwind CSS + ShadCN components
- Vite for dev/build

---

## Out of Scope (MVP)

- Real authentication/OAuth
- Payments or parts integrations
- Full table/list view
- Complex scheduling
- WIP limits on columns (stretch goal)
- RO deletion (trivial to add later)
