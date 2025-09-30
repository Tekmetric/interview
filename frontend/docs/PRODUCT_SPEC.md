# TekBoard – Product Specification

## 1) Summary
TekBoard is a Kanban-style board for an auto repair shop to visualize and manage Repair Orders (ROs) across the workflow. It includes a lightweight welcome/auth experience, a small dashboard for KPIs and quick lists, and a primary Kanban view with drag-and-drop, filtering, and an editable details drawer.

Primary view: Kanban Board. Dashboard is a concise overview, not a separate heavy table.

## 2) Goals (MVP)
- Surface the shop’s work-in-progress at a glance (5 statuses).
- Enable fast status changes via drag-and-drop with optimistic updates.
- Provide quick filters/search and delightful empty/loading states.
- Allow inline editing via a details drawer (assign tech, notes, priority).
- Include a welcoming entry experience (mock login/welcome back) suitable for an interview demo.

Non-goals (MVP):
- Full authentication/authorization; we will mock identity only.
- Payments, parts integrations, or complex scheduling.
- Full table view for all ROs (Dashboard provides short lists; Board is the primary working view).

## 3) Personas & Use Cases
- Service Advisor: triages new ROs, updates statuses, assigns technicians, monitors overdue.
- Shop Manager: scans KPIs (WIP, overdue, waiting parts), balances workload.

Primary Tasks:
- “See today’s workload, what’s late, what’s blocked.”
- “Update the status of an RO and assign a technician quickly.”
- “Find an RO by customer, plate, or RO number.”

## 4) Information Architecture
- Welcome/Auth (mocked) → Dashboard (KPI + quick lists) → Kanban Board (primary) → RO Details Drawer (edit) → Settings (minimal; optional).

## 5) Welcome / Auth (Mocked)
Two lightweight paths; recommend the first for interview:

Option A: Welcome Back (Mock Identity)
- Screen shows: brand, friendly welcome, “Continue as Demo User” primary button, and secondary social buttons (Google/Apple) that simply set a mock token/user in localStorage.
- Acceptance: Clicking any button stores `{ userId: "demo", name: "Alex" }` and routes to Dashboard.

Option B: Social Buttons (Fully Mocked)
- Same visuals as A, but clicking social triggers a fake `/api/auth/mock-login` that returns the same demo user. Implemented via mock/backend.

Out-of-scope: Real OAuth, password flows, or protected routes beyond simple guard.

## 6) Dashboard
Purpose: Quick pulse and shortcuts—not a full data table.

Components:
- KPI Cards (today/this week):
  - Total WIP (count across non-completed statuses)
  - Overdue (promisedAt < now)
  - Waiting Parts (count)
  - Awaiting Approval (count)
- Quick Lists (top 5 each):
  - Overdue ROs (RO #, customer, promisedAt, status)
  - Appointments Today (if seeded), or “New” ROs created in last 24h
- Quick Actions:
  - “Open Kanban Board” (primary)
  - “Create RO” (opens empty details drawer or a simple create screen)

Acceptance:
- Loads fast; shows skeletons while fetching.
- Each KPI links to the Board with pre-applied filters.

## 7) Kanban Board (Primary)
Statuses (columns): NEW, AWAITING_APPROVAL, IN_PROGRESS, WAITING_PARTS, COMPLETED.

Column Header:
- Title + count badge, optional WIP limit badge (stretch), per-column skeleton/loading and empty states.

Card Content (at-a-glance):
- RO number (e.g., #1001), customer name, vehicle Y/M/M, plate.
- Promised time (overdue highlight), technician avatar/initials if assigned.
- Priority/tag chips (e.g., Walk-in, High Priority).

DnD Behavior:
- Drag card between columns to change status.
- Optimistic update: move immediately; if backend rejects, card snaps back and a toast explains why.
- Keyboard accessibility: focus + key controls (as supported by the DnD library), announce via ARIA live region.

Filtering & Search:
- Search by customer/plate/RO #; filter by technician; sort by promisedAt.
- Filters persist in the URL (`?q=&tech=&sort=`) so the view is shareable.

## 8) RO Details Drawer (Edit)
Opens from card click. Allows minimal edits for MVP:
- Fields: status (select), technician (select), priority (normal/high), notes (textarea), tags (chips), promisedAt (datetime input, optional).
- Actions: Save (PATCH), Cancel (close, discard changes). Deleting RO is out-of-scope for MVP but trivial to add.

Acceptance:
- Form validation (required fields where applicable); invalid inputs show inline errors.
- Save uses optimistic mutation with rollback on failure; toast on success/failure.

## 9) List/Table View Decision
- MVP: No separate full table view. The Dashboard quick lists + Kanban provide discovery and action.
- Stretch: Add a compact table route (`/table`) for export/print with basic columns and filters.

## 10) Data Model
Entity: RepairOrder
```json
{
  "id": "ro-1001",
  "roNumber": "1001",
  "status": "NEW",
  "customerName": "Alex Perez",
  "vehicleYMM": "2018 Honda Accord",
  "plate": "ABC123",
  "promisedAt": "2025-10-01T20:00:00Z",
  "technicianId": null,
  "priority": "NORMAL",
  "tags": ["walk-in"],
  "notes": ""
}
```

Entity: Technician
```json
{ "id": "t-1", "name": "Sam Chen", "avatar": "", "skills": ["engine"] }
```

Status Transition Map (enforced FE+BE):
```json
{
  "NEW": ["AWAITING_APPROVAL", "IN_PROGRESS"],
  "AWAITING_APPROVAL": ["IN_PROGRESS", "NEW"],
  "IN_PROGRESS": ["WAITING_PARTS", "COMPLETED", "AWAITING_APPROVAL"],
  "WAITING_PARTS": ["IN_PROGRESS"],
  "COMPLETED": []
}
```

## 11) API (Minimal, for MVP)
Base path: `/api`

- GET `/repairOrders` – supports `q`, `status`, `technicianId`, `sort` (promisedAt, asc|desc).
- POST `/repairOrders` – create minimal RO.
- PATCH `/repairOrders/:id` – partial update (status, technicianId, notes, priority, promisedAt, tags).
- GET `/technicians` – list for assignment.

Error Contract (example):
```json
{
  "error": "INVALID_TRANSITION",
  "message": "Cannot move from WAITING_PARTS to NEW.",
  "from": "WAITING_PARTS",
  "to": "NEW",
  "allowed": ["IN_PROGRESS"]
}
```

## 12) UX States & Feedback
- Loading: Column skeleton cards; KPI skeletons.
- Empty: Friendly illustrations/messages; suggest actions (Create RO).
- Errors: Inline messages + retry buttons; toasts for mutations.
- Accessibility: Focus trap in drawer, ARIA live regions for moves/updates, sufficient color contrast.

## 13) Performance & Limits
- Target: 50–150 cards comfortably without virtualization.
- Avoid heavy re-renders: memoize cards, keep server state in react-query.
- Auto-scroll while dragging; smooth column scroll.

## 14) Visual Guidelines (MVP)
- Clean, legible typography; clear column headers and badges.
- Colors: neutral base, semantic accents for status (overdue) and priority.
- Subtle elevation on drag; clear drop targets.

## 15) Metrics (Demo-friendly)
- Time-in-status (derived, simple): show “Overdue by Xh”.
- DnD interactions count (for demo instrumentation only, optional).

## 16) Risks & Mitigations
- DnD complexity (accessibility, auto-scroll): choose mature library and keep columns simple.
- Backend availability: develop with local `json-server` or Express+file DB; handle failures with optimistic rollback.
- Scope creep: no table view in MVP; dashboard is intentionally light.

## 17) Demo Script (Interview)
1) Welcome screen → Continue as Demo User.
2) Dashboard: call out KPIs and quick lists; click “Overdue (5)” to open Board pre-filtered.
3) On Board: drag RO from Awaiting Approval → In Progress; show optimistic move + toast.
4) Open card: assign technician, add note; save; demonstrate rollback by forcing an error (optional seed rule).
5) Search by plate; show overdue highlight and column counts.

## 18) Open Questions
- Should Completed auto-hide after N days? (Default: show, filterable.)
- Should creation happen from dashboard or board only? (Default: from board.)
- Are WIP limits required in MVP? (Default: stretch.)

