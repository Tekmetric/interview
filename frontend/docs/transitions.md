# Stage Transition Rules

**Source of Truth**: Aligned with [specs.md](./specs.md) lines 120-129

---

## Workflow Stages

```
┌─────┐     ┌──────────────────┐     ┌─────────────┐     ┌──────────────┐     ┌───────────┐
│ NEW │ --> │ AWAITING_APPROVAL│ --> │ IN_PROGRESS │ --> │ WAITING_PARTS│ --> │ COMPLETED │
└─────┘     └──────────────────┘     └─────────────┘     └──────────────┘     └───────────┘
   ↑                  ↑                       ↑                     ↑
   └──────────────────┴───────────────────────┴─────────────────────┘
                  (Flexible backward movement allowed)
```

---

## Allowed Transitions

### Transition Map

```typescript
export const ALLOWED_TRANSITIONS: Record<RepairOrderStatus, RepairOrderStatus[]> = {
  NEW: ['AWAITING_APPROVAL', 'IN_PROGRESS'],
  AWAITING_APPROVAL: ['IN_PROGRESS', 'NEW'],
  IN_PROGRESS: ['WAITING_PARTS', 'COMPLETED', 'AWAITING_APPROVAL'],
  WAITING_PARTS: ['IN_PROGRESS'],
  COMPLETED: [],
}
```

### Valid Transitions

- ✅ NEW → AWAITING_APPROVAL (standard workflow)
- ✅ NEW → IN_PROGRESS (skip approval for urgent work)
- ✅ AWAITING_APPROVAL → IN_PROGRESS (approval granted)
- ✅ AWAITING_APPROVAL → NEW (customer declined)
- ✅ IN_PROGRESS → WAITING_PARTS (need parts)
- ✅ IN_PROGRESS → COMPLETED (work finished)
- ✅ IN_PROGRESS → AWAITING_APPROVAL (found additional work)
- ✅ WAITING_PARTS → IN_PROGRESS (parts arrived)

### Invalid Transitions

- ❌ NEW → COMPLETED (cannot skip all workflow stages)
- ❌ AWAITING_APPROVAL → WAITING_PARTS (must start work first)
- ❌ WAITING_PARTS → COMPLETED (must resume work before completion)
- ❌ COMPLETED → ANY (completed orders cannot be moved)

---

## Validation

### Frontend Validation

Client-side validation prevents invalid drag-and-drop operations:

```typescript
import { canTransition } from '@/lib/transitions'

const handleDragEnd = (event: DragEndEvent) => {
  const { active, over } = event
  const orderId = active.id as string
  const newStatus = over.id as RepairOrderStatus
  const order = orders?.find((o) => o.id === orderId)

  if (!order) return

  // Client-side validation
  const validation = canTransition(order.status, newStatus)
  if (!validation.allowed) {
    toast.error(validation.reason)
    return
  }

  // Optimistic update via TanStack Query
  moveOrder({ id: orderId, newStatus })
}
```

### Backend Validation

Server-side validation enforces business rules with 409 response:

```typescript
// server/routes/repairOrders.ts
router.patch('/repairOrders/:id', async (req, res) => {
  const { id } = req.params
  const updates = updateSchema.parse(req.body)

  const order = db.getRepairOrder(id)
  if (!order) {
    return res.status(404).json({ error: 'Repair order not found' })
  }

  // Validate status transition
  if (updates.status && updates.status !== order.status) {
    const validation = canTransition(order.status, updates.status)

    if (!validation.allowed) {
      return res.status(409).json({
        error: 'INVALID_TRANSITION',
        message: validation.reason,
        from: order.status,
        to: updates.status,
        allowed: ALLOWED_TRANSITIONS[order.status] || [],
      })
    }
  }

  const updated = db.updateRepairOrder(id, updates)
  res.json(updated)
})
```

---

## Error Handling

### 409 Conflict Response

When an invalid transition is attempted, the API returns:

```json
{
  "error": "INVALID_TRANSITION",
  "message": "Cannot move from WAITING_PARTS to NEW.",
  "from": "WAITING_PARTS",
  "to": "NEW",
  "allowed": ["IN_PROGRESS"]
}
```

### Frontend Error Display

The UI shows a toast notification with:

- Error message explaining why the transition is not allowed
- List of valid transitions from the current status
- Optional "Retry" action for network failures

---

## Workflow Philosophy

**Flexibility Over Rigidity**: The transition map allows flexible workflow movement while preventing illogical jumps:

- **Forward Flexibility**: NEW can skip directly to IN_PROGRESS for urgent jobs
- **Backward Movement**: Work can return to previous stages when issues are discovered
- **Terminal State**: COMPLETED orders cannot be moved (prevents accidental modifications)
- **Safety Net**: Both client and server validation prevent data corruption

**Real-World Scenarios**:

- **Urgent Job**: NEW → IN_PROGRESS (skip approval for walk-in customer)
- **Customer Declined**: AWAITING_APPROVAL → NEW (quote rejected, start over)
- **Found More Work**: IN_PROGRESS → AWAITING_APPROVAL (discovered additional issues)
- **Parts Delay**: IN_PROGRESS → WAITING_PARTS → IN_PROGRESS (temporary hold)

---

## Testing

### Test Cases

```typescript
// Shared transition validation (frontend & backend)
import { canTransition } from '@/lib/transitions'

// Valid transitions
expect(canTransition('NEW', 'AWAITING_APPROVAL').allowed).toBe(true)
expect(canTransition('NEW', 'IN_PROGRESS').allowed).toBe(true)
expect(canTransition('WAITING_PARTS', 'IN_PROGRESS').allowed).toBe(true)

// Invalid transitions
expect(canTransition('NEW', 'COMPLETED').allowed).toBe(false)
expect(canTransition('WAITING_PARTS', 'NEW').allowed).toBe(false)
expect(canTransition('COMPLETED', 'IN_PROGRESS').allowed).toBe(false)

// No-op (same status)
expect(canTransition('IN_PROGRESS', 'IN_PROGRESS').allowed).toBe(true)
```

### Integration Testing

- Backend returns 409 for invalid transitions
- Frontend shows error toast with allowed transitions
- Optimistic updates roll back on validation failure
- Drag-and-drop prevents invalid drops with visual feedback

---

## Notes

- All transition rules are defined once and shared between frontend and backend
- TypeScript ensures enum consistency across the application
- The validation function is stateless and easily testable
- See [API.md](./API.md) for complete endpoint documentation
- See [specs.md](./specs.md) for complete feature specification
