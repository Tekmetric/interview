# TekBoard - Auto Repair Shop Kanban System

> **Interview Project Submission** - Visual workflow management for repair orders

## 🎯 Project Overview

A drag-and-drop Kanban board for managing automotive repair orders through workflow stages, built to showcase modern frontend development skills while addressing Tekmetric's core product domain.

**Key Features**:

- ✅ Drag-and-drop repair orders between workflow stages (NEW → AWAITING_APPROVAL → IN_PROGRESS → WAITING_PARTS → COMPLETED)
- ✅ Dashboard with KPIs (Total WIP, Overdue, Waiting Parts, Awaiting Approval)
- ✅ Full CRUD backend API with Express + SQLite + TypeScript
- ✅ Real-time search & filtering (customer, vehicle, technician, priority)
- ✅ Stage transition validation (client + server, enforced per PRODUCT_SPEC)
- ✅ Optimistic UI updates with automatic rollback
- ✅ Overdue highlighting based on promisedAt timestamp
- ✅ Mobile-responsive design for shop floor tablets
- ✅ Accessibility features (keyboard navigation, ARIA labels, screen reader support)

**Tech Stack**:

- **Frontend**: React 19, TypeScript, TanStack Query v5, @dnd-kit, Tailwind CSS, shadcn/ui
- **Backend**: Express, SQLite3, Zod validation, TypeScript
- **State**: TanStack Query (server state), URL params (filter state)
- **Testing**: Vitest (unit + integration tests)

---

## 🚀 Quick Start

### Prerequisites

- Node.js 18+ (for native fetch)
- npm or yarn

### Installation

```bash
# Install dependencies
npm install

# Seed database with 50 repair orders
npm run seed

# Start both backend (port 3001) and frontend (port 5173)
npm run dev
```

### Usage

1. Backend API starts with 50 seeded repair orders
2. Navigate to http://localhost:5173
3. Drag cards between columns to update repair status
4. Click any card to view/edit full details
5. Use search/filter to find specific orders
6. Try invalid transitions to see validation in action

---

## 📚 Documentation

Detailed documentation in [`docs/`](./docs) folder:

- [**specs.md**](./docs/specs.md) - **Source of truth** for features, data model, workflow
- [**api.md**](./docs/api.md) - REST api specification (endpoints, request/response schemas)
- [**transitions.md**](./docs/transitions.md) - Stage transition rules and validation logic

---

## 💡 Why This Approach?

### Business Alignment

Tekmetric's product is auto repair shop management software. This Kanban board directly mirrors their workflow management features, demonstrating both technical skill and product understanding.

### Technical Decisions

**Modern Stack (React 19 + TypeScript)**:

- Type safety across frontend and backend
- Shared validation logic prevents drift
- Current industry best practices

**TanStack Query v5**:

- Automatic caching and request deduplication
- Built-in optimistic updates with rollback
- No manual `localStorage` or Context API state management needed

**@dnd-kit over react-beautiful-dnd**:

- Actively maintained (rbd is in maintenance mode)
- First-class TypeScript support
- Smaller bundle size (15KB vs 40KB)
- Modular, tree-shakeable architecture

**Tailwind + shadcn/ui**:

- Production-ready components we own (no package bloat)
- Fast iteration without CSS file switching
- Accessible by default (Radix UI primitives)

**Shared Transition Logic**:

- Same validation function used in frontend and backend
- TypeScript ensures enum consistency
- Backend returns 409 with allowed transitions on invalid moves

**URL State for Filters**:

- Shareable links with filters applied (`/?q=honda&tech=TECH-001`)
- Browser back/forward works correctly
- Demonstrates thoughtful UX design

---

## 🏗️ Architecture Highlights

### Backend (Express + TypeScript)

- RESTful API with proper HTTP status codes
- Zod schema validation on all endpoints
- Stage transition enforcement (returns 409 for invalid moves)
- SQLite for zero-config persistence
- 50 seeded repair orders with realistic data

### Frontend (React 19 + TypeScript)

- TanStack Query for server state (caching, optimistic updates)
- @dnd-kit for drag-and-drop (keyboard accessible)
- URL query params for filter state (shareable links)
- shadcn/ui components (accessible, customizable)
- Debounced search, loading skeletons, error boundaries

### Validation Strategy

```typescript
// Shared between frontend and backend
export function canTransition(from: Status, to: Status, order?: Order): ValidationResult {
  // Business rules: can't skip stages, requires tech for work, etc.
  // Frontend: prevent invalid drops with toast
  // Backend: enforce with 409 response
}
```

---

## 🎨 UX Polish

- **Loading States**: Skeleton cards while fetching
- **Empty States**: Friendly messages in empty columns
- **Toast Notifications**: Success/error feedback with actions
- **Overdue Highlighting**: Red border for overdue repair orders
- **Priority Badges**: Color-coded (urgent=red, normal=amber, routine=green)
- **Real-time Stats**: Column counts, in-progress totals
- **Responsive Design**: Horizontal scroll on tablet, single column on mobile

---

## ♿ Accessibility

- **Keyboard Navigation**: Tab through cards, Space to pick up/drop
- **Screen Reader Support**: ARIA labels, live region for drag announcements
- **Focus Management**: Visible focus indicators, modal focus trap
- **Color Contrast**: WCAG AA compliance
- **Semantic HTML**: Proper heading hierarchy, landmark regions

---

## 🧪 Testing

```bash
# Run all tests
npm test

# Run backend tests only
npm run test:server

# Run with coverage
npm run test:coverage
```

**Test Coverage**:

- Unit tests for transition validation logic
- Integration tests for API endpoint validation
- Manual testing across Chrome, Firefox, Safari
- Mobile testing on iOS/Android

---

## 🔮 Future Enhancements

If this were a production feature:

- **Real-time Collaboration**: WebSocket updates (multi-user)
- **Time Tracking**: Automatic stage duration analytics
- **Customer Notifications**: SMS alerts on status changes
- **VIN Decoder Integration**: Auto-populate vehicle details from VIN
- **Digital Inspection**: Photo checklist workflow
- **Advanced Analytics**: Bottleneck detection, tech productivity charts
- **Mobile App**: React Native version for shop floor use
- **Offline Mode**: IndexedDB + service worker sync

---

## 📸 Screenshots

[TODO: Add screenshots before PR submission]

- Full board view with 5 columns
- Card detail modal
- Mobile responsive layout
- Error toast notification

---

## 🤝 Acknowledgments

Built for Tekmetric interview by [Your Name]

**Technologies**:

- [React](https://react.dev/) - UI library
- [TanStack Query](https://tanstack.com/query) - Server state management
- [@dnd-kit](https://dndkit.com/) - Drag and drop
- [Tailwind CSS](https://tailwindcss.com/) - Utility-first CSS
- [shadcn/ui](https://ui.shadcn.com/) - Accessible component library
- [Zod](https://zod.dev/) - TypeScript-first validation

---

## 📄 Original Assignment

### Steps to get started:

#### Fork the repository and clone it locally

- https://github.com/Tekmetric/interview.git

#### Let's install the project locally

`npm install`

#### Let's start the project locally

`npm start` (or `npm run dev` for full-stack)

### Goals

1. ✅ Fetch Data from the backend CRUD API you created
2. ✅ Display data from API onto your page (Kanban board)
3. ✅ Apply a styling solution (Tailwind + shadcn/ui)
4. ✅ Have fun (drag-and-drop, validation, polish!)

### Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
