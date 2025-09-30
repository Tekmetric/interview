# API Documentation

**Base URL**: `/api`

**Source of Truth**: Aligned with [PRODUCT_SPEC.md](./PRODUCT_SPEC.md) lines 131-148

---

## Endpoints

### GET `/repairOrders`
Retrieve all repair orders with optional filtering.

**Query Parameters**:
- `q` (string, optional) - Search by customer name, plate, or RO number
- `status` (string, optional) - Filter by status: `NEW`, `AWAITING_APPROVAL`, `IN_PROGRESS`, `WAITING_PARTS`, `COMPLETED`
- `technicianId` (string, optional) - Filter by assigned technician ID (e.g., `t-1`)
- `sort` (string, optional) - Sort field: `promisedAt`, `createdAt`, `roNumber` (default: `promisedAt`)
- `order` (string, optional) - Sort order: `asc` or `desc` (default: `asc`)

**Response**: `200 OK`
```json
[
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
]
```

---

### POST `/repairOrders`
Create a new repair order.

**Request Body**:
```json
{
  "customerName": "Jane Doe",
  "vehicleYMM": "2020 Toyota Camry",
  "plate": "XYZ789",
  "promisedAt": "2025-10-02T14:00:00Z",
  "technicianId": "t-1",
  "priority": "HIGH",
  "tags": ["appointment"],
  "notes": "Customer mentioned check engine light"
}
```

**Response**: `201 Created`
```json
{
  "id": "ro-1052",
  "roNumber": "1052",
  "status": "NEW",
  "customerName": "Jane Doe",
  "vehicleYMM": "2020 Toyota Camry",
  "plate": "XYZ789",
  "promisedAt": "2025-10-02T14:00:00Z",
  "technicianId": "t-1",
  "priority": "HIGH",
  "tags": ["appointment"],
  "notes": "Customer mentioned check engine light",
  "createdAt": "2025-10-01T10:30:00Z",
  "updatedAt": "2025-10-01T10:30:00Z"
}
```

---

### PATCH `/repairOrders/:id`
Update an existing repair order (partial update).

**Request Body** (all fields optional):
```json
{
  "status": "IN_PROGRESS",
  "technicianId": "t-2",
  "priority": "HIGH",
  "tags": ["walk-in", "urgent"],
  "notes": "Found worn brake pads, needs replacement",
  "promisedAt": "2025-10-01T16:00:00Z"
}
```

**Response**: `200 OK` (updated repair order)

**Error Response** (Invalid Transition): `409 Conflict`
```json
{
  "error": "INVALID_TRANSITION",
  "message": "Cannot move from WAITING_PARTS to NEW.",
  "from": "WAITING_PARTS",
  "to": "NEW",
  "allowed": ["IN_PROGRESS"]
}
```

---

### GET `/technicians`
Retrieve all technicians.

**Response**: `200 OK`
```json
[
  {
    "id": "t-1",
    "name": "Sam Chen",
    "avatar": "",
    "skills": ["engine"]
  },
  {
    "id": "t-2",
    "name": "Alex Rivera",
    "avatar": "",
    "skills": ["transmission"]
  }
]
```

---

## Data Model

### RepairOrder
```typescript
interface RepairOrder {
  id: string;              // "ro-1001"
  roNumber: string;        // "1001"
  status: RepairOrderStatus;
  customerName: string;    // "Alex Perez"
  vehicleYMM: string;      // "2018 Honda Accord"
  plate: string | null;    // "ABC123"
  promisedAt: string | null; // ISO 8601 timestamp
  technicianId: string | null; // "t-1"
  priority: 'HIGH' | 'NORMAL';
  tags: string[];          // ["walk-in"]
  notes: string;
  createdAt: string;       // ISO 8601
  updatedAt: string;       // ISO 8601
}

type RepairOrderStatus =
  | 'NEW'
  | 'AWAITING_APPROVAL'
  | 'IN_PROGRESS'
  | 'WAITING_PARTS'
  | 'COMPLETED';
```

### Technician
```typescript
interface Technician {
  id: string;       // "t-1"
  name: string;     // "Sam Chen"
  avatar: string;   // URL or empty string
  skills: string[]; // ["engine"]
}
```

---

## Error Responses

### 400 Bad Request
Invalid request body or query parameters.
```json
{
  "error": "Validation error",
  "details": [
    {
      "path": ["customerName"],
      "message": "Required"
    }
  ]
}
```

### 404 Not Found
Repair order or technician not found.
```json
{
  "error": "Repair order not found"
}
```

### 409 Conflict
Invalid status transition (see [TRANSITIONS.md](./TRANSITIONS.md) for rules).
```json
{
  "error": "INVALID_TRANSITION",
  "message": "Cannot move from WAITING_PARTS to NEW.",
  "from": "WAITING_PARTS",
  "to": "NEW",
  "allowed": ["IN_PROGRESS"]
}
```

---

## Notes

- All timestamps are in ISO 8601 format (e.g., `2025-10-01T20:00:00Z`)
- The `roNumber` field is auto-generated if not provided during creation
- Status transitions are validated on both frontend and backend (see [TRANSITIONS.md](./TRANSITIONS.md))
- The API enforces the transition rules defined in [PRODUCT_SPEC.md](./PRODUCT_SPEC.md) lines 120-129
