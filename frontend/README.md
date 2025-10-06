# Pokédex

A modern, performant Pokédex application built with React that displays data from the PokéAPI. Features virtualized scrolling for optimal performance with 1300+ Pokémon, real-time search, and responsive design.

## Features

- **Complete Pokédex**: Browse all 1302 Pokémon from the PokéAPI
- **Virtualized Scrolling**: Smooth performance with react-window for handling large datasets
- **Real-time Search**: Filter by name, number, or type with instant results
- **Responsive Design**: Optimized layouts for mobile and desktop
- **Keyboard Shortcuts**: Press `Cmd/Ctrl + F` to quickly focus the search
- **Type Badges**: Visual type indicators with color-coded badges
- **Accessibility**: ARIA labels, keyboard navigation, and skip links
- **Error Handling**: Graceful error states with retry functionality
- **API Health Checks**: Validates API connectivity before data fetching

## Tech Stack

- **React** (16.8.1) - UI framework with Hooks
- **react-window** - Virtualized list for performance
- **Tailwind CSS** - Utility-first styling
- **Emotion** - CSS-in-JS for dynamic styling
- **PokéAPI** - RESTful Pokémon data API
- **Jest & React Testing Library** - Unit testing

## Installation

```bash
# Install dependencies
npm install

# Start development server
npm start

# Run tests
npm test

# Build for production
npm run build
```

## Development

The app runs on [http://localhost:3000](http://localhost:3000) in development mode.

### Project Structure

```
src/
├── components/
│   └── Table.js          # Virtualized table component
├── App.js                # Main application component
├── data.js               # API fetching logic
├── data.test.js          # API tests
└── styles.js             # Tailwind CSS classes
```

### Key Components

- **App.js**: Main container with search, state management, and layout
- **Table.js**: Virtualized table using react-window for performance
- **data.js**: API integration with health checks and batch fetching

## Testing

```bash
npm test
```

Tests include:
- API health check validation
- Pokemon data fetching
- Error handling scenarios

## Deployment

### Vercel (Recommended)

```bash
# Install Vercel CLI
npm install -g vercel

# Deploy to preview
vercel

# Deploy to production
vercel --prod
```

Live deployment: [https://frontend-lhegmexue-jonyens-projects.vercel.app](https://frontend-lhegmexue-jonyens-projects.vercel.app)

### Other Platforms

The app is a standard Create React App and can be deployed to any static hosting platform (Netlify, GitHub Pages, AWS S3, etc.).

## Performance Optimizations

- Virtualized rendering with react-window (renders only visible rows)
- Dynamic row heights for optimal space usage
- Batch API requests with Promise.all
- Memoized search filtering
- Responsive image loading

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

This project was created as a coding exercise for Tekmetric.

## Acknowledgments

- [PokéAPI](https://pokeapi.co/) for the comprehensive Pokémon data
- Pokémon and Pokémon character names are trademarks of Nintendo
