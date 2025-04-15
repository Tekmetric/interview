# Tekmetric Car Repair Shop Management System

## Project Overview

This is a modern web application for managing car repair services. The application allows repair shops to track service requests, manage customer information, and monitor repair status throughout the service lifecycle.

## Features

- **Authentication**: Secure login using Auth0 integration
- **Repair Service Management**: Create, read, update, and delete repair service records
- **Responsive UI**: Mobile-friendly interface built with Tailwind CSS
- **Data Table**: Interactive table with sorting, pagination, and filtering capabilities
- **Status Tracking**: Visual status indicators for repair progress
- **User Profile**: Detailed user information page

## Tech Stack

- **Frontend**: React, TypeScript, Tailwind CSS
- **State Management**: React Context API, SWR for data fetching and caching
- **UI Components**: Custom components with Tailwind styling
- **Authentication**: Auth0 integration
- **Table**: TanStack Table (React Table)
- **API Communication**: Fetch API with custom hooks

## Getting Started

### Prerequisites

- Node.js (v14 or later)
- npm or yarn

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/emregozen/tekmetric-interview.git
   cd tekmetric-interview/frontend
   ```

2. Install dependencies
   ```bash
   npm install
   ```

3. Set up environment variables
   - Create a `.env` file in the frontend directory
   - Add the following variables (replace with your Auth0 credentials):
   ```
   REACT_APP_AUTH0_DOMAIN=your-auth0-domain
   REACT_APP_AUTH0_CLIENT_ID=your-auth0-client-id
   REACT_APP_AUTH0_CALLBACK_URL=http://localhost:3000/callback
   REACT_APP_API_SERVER_URL=http://localhost:8080
   REACT_APP_AUTH0_AUDIENCE=your-auth0-audience
   ```

4. Start the development server
   ```bash
   npm start
   ```

5. Open [http://localhost:3000](http://localhost:3000) in your browser

## Project Structure

- `/src/components` - Reusable UI components
- `/src/components/layout` - Layout components (Header, Navbar, Footer)
- `/src/components/svg` - SVG icon components
- `/src/components/ui` - Basic UI components (Input, Select, etc.)
- `/src/hooks` - Custom React hooks for API operations
- `/src/pages` - Page components
- `/src/types` - TypeScript type definitions
- `/src/utils` - Utility functions

## API Integration

The application uses a set of custom hooks to interact with the backend API:

- `useFetchRepairServices` - Fetches repair services with pagination and sorting
- `useCreateRepairService` - Creates new repair services
- `useUpdateRepairService` - Updates existing repair services
- `useDeleteRepairService` - Deletes repair services

## Future Enhancements

### To Be Added

- **I18n**: Internationalization support for multiple languages
- **Service Worker**: Offline support and better handling of internet connection issues
- **Advanced Filtering**: More sophisticated filtering options for the repair services table
- **Unit Tests**: Unit tests for the application to ensure code quality
- **Vite and Vitest**: Switch from Create React App to Vite for better performance and Vitest for testing
- **PNPM**: Switch to pnpm package manager for better performance and disk space efficiency
