# SpaceX Launch Dashboard

[Visit the SpaceX Launch Dashboard](https://tekmetric-interview.vercel.app/)

## Project Overview

This project is a SpaceX Launch Dashboard that provides real-time information (almost real-time :) More like Back to the future information, as API was cut on 2022) about SpaceX launches, including upcoming launches, latest launches, and historical data. It's built with modern web technologies and offers a responsive, user-friendly interface for space enthusiasts and professionals alike.

## Technologies Used

- **Next.js**: React framework for server-side rendering and static site generation
- **React**: JavaScript library for building user interfaces
- **TypeScript**: Typed superset of JavaScript for improved developer experience
- **Tailwind CSS**: Utility-first CSS framework for rapid UI development
- **SWR**: React Hooks library for data fetching
- **Jest**: JavaScript testing framework
- **Playwright**: Browser automation library for testing
- **Storybook**: Tool for developing UI components in isolation
- **ESLint**: JavaScript linting utility
- **Prettier**: Code formatter
- **Docker**: Containerization platform for building, shipping, and running applications in containers
- **GitHub Actions**: CI/CD platform for automating the build, test, and deployment of your application

## Cloud provider, Analytics and Speed Reports

- **Vercel**: Cloud platform for static sites and Serverless Functions, providing seamless deployment and hosting for Next.js applications.
- **Vercel Analytics**: A feature that provides real-time insights into your application's performance, allowing you to monitor metrics such as page load times and user interactions.
  ---------------------------------------<details>
    <summary>Web Analytics</summary>

  ![Web Analytics](https://i.ibb.co/4RhtvXGX/Screenshot-2025-02-03-at-00-25-53.png)
  </details>

- **Vercel Speed Reports**: Tools that help you analyze the speed of your application, offering suggestions for optimization to enhance user experience and performance.
  ---------------------------------------<details>
    <summary>Speed Reports</summary>

  ![Web Analytics](https://i.ibb.co/236Crgd5/Screenshot-2025-02-03-at-00-25-30.png)
  </details>

## Getting Started

### Prerequisites

- Node.js (v18 or later)
- npm (v7 or later)

### Installation

1. Clone the repository:

```
git clone [git@github.com:SerhiiYakovenko/tekmetric-interview.git](git@github.com:SerhiiYakovenko/tekmetric-interview.git)
```

2. Navigate to the project directory:

```

cd frontend
```

3. Install dependencies:

```
npm install
```

## Running the Development Server

To start the development server:

```

npm run dev
```

- Open [http://localhost:3000](http://localhost:3000) in your browser to view the application.

## Building for Production

To create a production build:

```

npm run build
```

To start the production server:

```

npm start
```

## Running Tests

This project uses Jest and React Testing Library for unit and integration tests, and Playwright for end-to-end testing.

To run unit tests:

```
npm run test:unit
```

To run end-to-end tests with Playwright:

```
npm run test:e2e
```

## Using Storybook

Storybook is used for developing and showcasing UI components in isolation.

To start Storybook:

```

npm run storybook
```

This will open Storybook in your default browser, typically at [http://localhost:6006](http://localhost:6006).

## Linting and Formatting

To run ESLint:

```
npm run lint
```

To fix auto-fixable ESLint issues:

```

npm run lint:fix
```

To format code with Prettier:

```

npm run format
```

## Docker

To build and run the application using Docker:

```
docker build -t spacex-dashboard .
docker run --rm -p 3000:3000 -p 6006:6006 spacex-dashboard
```

## CI/CD

This project uses GitHub Actions for continuous integration and deployment. The workflow is defined in `.github/workflows/ci.yml`.

## Project Structure

```

frontend/
├── .github/             # GitHub Actions workflows
├── .storybook/          # Storybook configuration
├── public/              # Static files
├── src/
│   ├── app/             # Next.js app directory
│   │   ├── api/         # API routes
│   │   ├── components/  # React components
│   │   ├── hooks/       # Custom React hooks
│   │   ├── lib/         # Utility functions and API clients
│   │   ├── types/       # TypeScript type definitions
│   │   ├── layout.tsx   # Layout component
│   │   └── page.tsx     # Home page component
│   └── styles/          # Global styles
├── tests/
│   ├── e2e/             # E2E testing with Playwright
├── .eslintrc.js         # ESLint configuration
├── .prettierrc          # Prettier configuration
├── Dockerfile           # Docker configuration
├── jest.config.js       # Jest configuration
├── next.config.js       # Next.js configuration
├── package.json         # Project dependencies and scripts
├── README.md            # Project documentation (this file)
├── tailwind.config.js   # Tailwind CSS configuration
└── tsconfig.json        # TypeScript configuration
```
