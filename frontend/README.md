A React TypeScript application for user management with a clean, modern interface. Built with React 18, TypeScript, Tailwind CSS, and React Query.

## Scope

### In Scope (Implemented)
- **Core Functionality** - User CRUD operations with real-time search and filtering
- **Modern Tech Stack** - React 18, TypeScript 5.x, Vite, Tailwind CSS
- **State Management** - React Query for server state, Context for client state
- **Form Validation** - Zod schema validation with real-time feedback
- **Testing Foundation** - Vitest + React Testing Library with comprehensive test coverage
- **Code Quality** - ESLint, Prettier, TypeScript strict mode
- **Responsive Design** - Mobile-first approach with Tailwind breakpoints
- **Theme System** - Dark/light mode with system preference detection
- **Developer Experience** - Hot reload, TypeScript intellisense, VSCode settings
- **Error Handling** - Toast notifications, loading states, error boundaries
- **Advanced Toast System** - Multi-type notifications with auto-dismiss and stacking
- **Accessibility Basics** - Semantic HTML, ARIA attributes, keyboard navigation
- **Environment Management** - `.env` files for different environments
- **URL State Management** - Shareable URLs with search parameters
- **Basic SEO** - Meta tags, Open Graph, structured HTML

### Out of Scope (Future Work)
- **Localization (i18n)** - No internationalization support
- **Integration Testing** - No end-to-end or integration tests
- **CI/CD Pipeline** - No GitHub Actions or automated deployment
- **Performance Monitoring** - No analytics, error tracking, or performance metrics
- **Telemetry & Analytics** - No usage tracking or user behavior analytics
- **Feature Flags** - No A/B testing or feature toggle system
- **User Feedback System** - No feedback button or user survey mechanisms
- **Code Splitting** - No lazy loading
- **Pre-commit Hooks** - No Husky or lint-staged setup
- **Advanced Accessibility** - No screen reader testing or WCAG 2.1 AA compliance

## Features

### User Management
- **Full CRUD Operations** - Create, read, update, and delete users
- **Form Validation** - Real-time validation with Zod schema validation
- **Data Caching** - Intelligent caching with React Query for optimal performance

### Search & Discovery  
- **Real-time Search** - Search across user fields (name, email, company)
- **Status Filtering** - Filter users by Active/Inactive status
- **Column Sorting** - Sort by any column with ascending/descending options
- **Pagination** - Configurable page sizes (5, 10, 20, 50 users per page)
- **URL State Management** - Bookmark and share search results via URL parameters

### User Experience
- **Responsive Design** - Mobile-first design for all screen sizes
- **Dark/Light Theme** - System preference detection with manual toggle
- **Loading States** - Smooth loading indicators and error states
- **Confirmation Dialogs** - Safe deletion with confirmation modals

### Technical Features
- **Toast Notifications** - Success, error, and info messages with auto-dismiss
- **Error Handling** - Graceful error recovery with retry mechanisms

## Tech Stack

### Core Technologies
- **React 18** - Modern React with hooks and concurrent features
- **TypeScript 5.x** - Static type checking for enhanced developer experience
- **Vite** - Fast build tool with HMR and optimized bundling

### UI & Styling
- **Tailwind CSS** - Utility-first CSS framework for rapid development
- **Heroicons** - Beautiful hand-crafted SVG icons
- **Responsive Design** - Mobile-first approach with breakpoint-based layouts

### State Management & Data
- **React Query (TanStack Query)** - Data synchronization with caching and background updates
- **React Context** - Global state for theme and notifications
- **Zod** - TypeScript-first schema validation

### Routing & Navigation
- **React Router v6** - Declarative routing with URL state management
- **Dynamic Routes** - User detail pages with parameter-based navigation

### Testing & Development
- **Vitest** - Fast unit testing framework with Jest-compatible API
- **React Testing Library** - Component testing with user-centric approach
- **ESLint** - Code linting with TypeScript and React rules
- **Prettier** - Consistent code formatting

## Quick Start

```bash
# Install dependencies
npm install

# Start development server
npm run dev
```

## Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run test` - Run tests
- `npm run lint` - Check code quality