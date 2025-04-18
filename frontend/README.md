# Tekmetric Interview Project

## Project Overview

This project consists of a full-stack application with a Spring Boot backend and a React frontend. The backend provides a CRUD API for managing Vehicle data, while the frontend displays this data in a user-friendly interface.

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Node.js 16 or higher
- npm 8 or higher

## Backend Setup and Running

1. Navigate to the backend directory:

```bash
cd backend
```

2. Clean and install dependencies:

```bash
mvn clean install
```

3. Build and run the backend server using one of these methods:

   a. Using Maven Spring Boot plugin:

   ```bash
   mvn spring-boot:run
   ```

   b. Or build the JAR and run it:

   ```bash
   mvn package
   java -jar target/interview-1.0-SNAPSHOT.jar
   ```

The backend server will start on port 8080.

## Frontend Setup and Running

1. Navigate to the frontend directory:

```bash
cd frontend
```

2. Install dependencies:

```bash
yarn
```

3. Start the development server:

```bash
yarn start
```

The frontend will start on port 5173, which is configured to work with the backend's CORS settings.

## Implementation Details

### Backend

- Built with Spring Boot
- Provides RESTful CRUD API endpoints for vehicle data
- Supports paginated and searchable vehicle listings via query parameters
- Uses H2 in-memory database for lightweight, self-contained persistence
- Pagination metadata included in all relevant responses
- CORS configured to allow requests from the frontend (`localhost:5173`)

### Frontend

- Built with React 18 and [Vite](https://vitejs.dev/) for fast development and optimized builds
- Uses modern React hooks for state management
- Custom mobile-first CSS
- Communicates with backend API for data operations
- Query-based pagination and search
- Global error handling and notifications
- Skeleton loading for list view
- CRUD Operations:
  - Create
  - Read (List + Form Population)
  - Update (Form Population)
  - Delete (Optimistic)
- Vitest / React Testing Library Snapshot Tests

### Post-MVP Future Improvements

#### Frontend

- Expand test coverage beyond snapshot tests
- Bubble up backend validations inline to the form
- Migrate to [RTK createSlice](https://redux-toolkit.js.org/api/createSlice) and [RTK Query](https://redux-toolkit.js.org/rtk-query/overview) for state management and data fetching
- Evaluate and migrate to an industry-standard CSS framework (e.g. [Tailwind CSS](https://tailwindcss.com/)) for improved scalability and consistency
- Implement lazy loading / Suspense
- Add API caching for list and item views
- Review and improve loading and error states
- Add form skeleton states
- Introduce E2E testing (e.g. Cypress or Playwright)

#### Backend

- Testing implementation(s)
- DTO (data transfer objects)
- Enhanced Vehicle Validation(s)
- S3 Image Uploads

## Goals

1. Fetch Data from the backend CRUD API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

## Submitting your coding exercise

Once you have finished the coding exercise please create a PR into Tekmetric/interview
