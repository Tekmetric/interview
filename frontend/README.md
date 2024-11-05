# FA

## Overview

FA is a web application that allows users to explore different exercises targeting specific muscle groups. Users can filter exercises by difficulty level and muscle group, making it easy to create tailored workout plans. This project is intended as a demonstration of skills in frontend development, API integration, and responsive design.

## Features

- **Exercise List**: Displays a list of exercises based on the selected muscle group.
- **Filtering**: Users can filter exercises by muscle group and difficulty level (beginner, intermediate, expert).
- **Loading and Error Handling**: Visual indicators for loading data and error messages when issues occur.
- **Mock Data**: The app includes local sample data for muscle groups to provide functionality without requiring an API key.

## Tech Stack

### Frontend

- **React**: Core library for building user interfaces.
- **Next.js**: Framework used to simplify server-side rendering and routing.
- **TypeScript**: For type safety and better developer experience.
- **Tanstack React Query**: Manages server-state and handles data fetching logic efficiently.
- **Axios**: HTTP client for making requests to the API.
- **Tailwind CSS & DaisyUI**: CSS frameworks for styling components and making the app visually appealing.

### Backend

- The backend is mocked using static JSON files that provide sample data for different muscle groups.
- A proxy API is configured to support CORS when fetching exercises from an external public API (if available).

## Project Setup

### Prerequisites

- **Node.js** and **npm** (or **Yarn**) installed.
- **React App** requires a `.env` file for accessing the external API (optional).

### Installation

1. Install dependencies:

   ```sh
   yarn install
   # or
   npm install
   ```

2. Create a `.env` file based on `.env.example` and add your API key if available:

   ```
   REACT_APP_RAPIDAPI_KEY=YOUR_API_KEY_HERE
   ```

3. Run the application:

   ```sh
   yarn dev
   # or
   npm run dev
   ```

### Running Tests

The application includes unit tests to ensure components work as expected. To run the tests:

```sh
yarn test
# or
npm run test
```

## Environment Variables

- **REACT_APP_RAPIDAPI_KEY**: Your API key for accessing external exercises API. If no key is provided, the app will fall back to using local sample data.

## Using Sample Data

If you do not have an API key, the application will use the mock data available in the `sample-data/` directory. The sample data includes exercises for all major muscle groups and is used automatically if the API key is missing.

## Usage

1. Start the by running \`yarn dev\` and open it in your browser at `http://localhost:3000`.
2. Use the dropdown menu to select a muscle group you want to focus on.
3. Apply filters for exercise difficulty if needed.
4. Browse the list of exercises for instructions and equipment needed.

## Testing

The project includes unit tests written with **React Testing Library** and **Jest**. The main components are tested to ensure proper rendering of loading states, error messages, and data filtering.
