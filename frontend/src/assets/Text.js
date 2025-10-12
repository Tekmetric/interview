const Text = {
  home: {
    title: "Home Page",
    subtitle: "Welcome to the interview app!",
    description:
      "This is the home page. Use the navigation above to explore different sections. The app now features Material-UI components with light/dark theme support!",
    projectRequirementsTitle: "Project Requirements:",
    requirements: {
      fetchData: {
        primary: "Fetch Data from a public API",
        linkText: "View API Samples",
        linkUrl: "https://github.com/toddmotto/public-apis",
      },
      displayData: {
        primary: "Display data from API onto your page (Table, List, etc.)",
      },
      styling: {
        primary:
          "Apply a styling solution of your choice to make your page look different",
        secondary: "Using Material-UI (MUI) with CSS-in-JS and theme support",
      },
    },
  },
  birdData: {
    title: "eBird Data",
    subtitle: "Recent bird observations from the eBird API 2.0",
    apiKeyError:
      "Make sure you have a valid eBird API key set in your environment variables (REACT_APP_EBIRD_API_KEY)",
    noDataMessage: "No observations found for the selected criteria",
    noDataSubtext: "Try adjusting the region, date range, or data type",
    controls: {
      dataType: "Data Type",
      region: "Region",
      daysBack: "Days Back",
      maxResults: "Max Results",
      refresh: "Refresh",
    },
    dataTypes: {
      recent: "Recent Observations",
      notable: "Notable Observations",
    },
    tableHeaders: {
      species: "Species",
      scientificName: "Scientific Name",
      location: "Location",
      date: "Date",
      count: "Count",
      observer: "Observer",
    },
    chips: {
      exotic: "Exotic",
    },
  },
  header: {
    title: "Interview App",
  },
  navigation: {
    home: "Home",
    about: "About",
    birdData: "Bird Data",
    species: "Species Explorer",
    hotspots: "Hotspots",
    regions: "Regions",
    activity: "Recent Activity",
  },
  notFound: {
    title: "404 - Page Not Found",
    subtitle: "Oops! The page you're looking for doesn't exist.",
    description:
      "The page you requested could not be found. It might have been moved, deleted, or you entered the wrong URL.",
    homeButton: "Go to Home",
    backButton: "Go Back",
    suggestions: {
      title: "Here are some suggestions:",
    },
  },
};

export default Text;
