# Tech Interview Project

# The Book Bazzar Application

## Overview
The Book Bazaar Application is a React-based web application that allows users to search for books, view book details, and navigate through categories using infinite scrolling. The app leverages the Google Books API to fetch and display book data. It also incorporates state management, data caching, and optimized image loading.

## Features
### 1. Book Fetching
- **Google Books API Integration**: The application fetches book data from the Google Books API.
- **Infinite Scrolling**: Users can continuously scroll to load more books.

### 2. Search Functionality
- **Dynamic Search**: Users can search for books by title, author, or keywords.
- **Clear Search**: A clear button is available to reset the search and return to the default category view.

### 3. Sidebar with Categories
- **Category Navigation**: A sidebar allows users to navigate through predefined book categories.
- **Default Category**: The app loads with a default category selected (e.g., Fiction).

### 4. Infinite Scrolling
- **Continuous Loading**: Books are loaded in batches as the user scrolls down.
- **Loading Indicators**: Visual indicators show when new data is being loaded.

### 5. Data and Image Loading Optimizations
- **Caching**: Category data is cached to reduce redundant API calls and improve performance.
- **Lazy Loading**: Images are lazy-loaded to optimize performance and user experience.

### 6. React Hooks and State Management
- **useState**: Used for local state management within components.
- **useReducer**: Manages global state through a reducer function, allowing complex state logic.
- **useContext**: Provides a global state across components using the BooksContext.
- **useQuery**: From React Query, used to fetch and manage server-side data.

### 7. Toast Notifications
- **Success and Error Notifications**: Provides feedback to users on successful data fetches or errors using react-toastify.

### 8. Error Handling
- **Graceful Degradation**: Proper error messages are displayed when API calls fail.

## Installation
- **Clone the Repository**
`npm install --legacy-peer-deps`

- **Add GOOGLE_API_KEY to the code**
Replace <GOOGLE_API_KEY> in api.ts file with the key I provided via email

- **Let's start the project locally**
`npm start`

- **Run tests**
`npm test`

## Project Structure

- **src/components**: Contains all React components (e.g., `BookTable`, `BookListTable`, `Sidebar`).
- **src/context**: Contains the context and provider for global state management (`BooksContext`).
- **src/utils**: Utility functions for API calls, caching, and debouncing.
- **src/tests**: Contains all test files for components using Jest and React Testing Library.
