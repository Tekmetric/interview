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
  species: {
    title: "Species Explorer",
    subtitle: "Explore bird species from the eBird taxonomy",
    loading: "Loading species data...",
    error: "Failed to load species data. Please try again.",
    tableHeaders: {
      commonName: "Common Name",
      scientificName: "Scientific Name",
      category: "Category",
      order: "Order",
      family: "Family",
      actions: "Actions",
    },
    pagination: {
      rowsPerPage: "Rows per page:",
      of: "of",
    },
    noData: "No species data available",
    searchPlaceholder: "Search species...",
    resultsText: {
      showing: "Showing",
      of: "of",
      species: "species",
      matching: "matching",
    },
  },
  speciesDetail: {
    loading: "Loading species details...",
    backButton: "Back to Species List",
    notFound: {
      title: "Species not found. The species code",
      message: "does not exist in our database.",
    },
    sections: {
      speciesInformation: "Species Information",
      recentObservations: "Recent Observations (US)",
    },
    fields: {
      category: "Category:",
      speciesCode: "Species Code:",
      order: "Order:",
      familyCommon: "Family (Common):",
      familyScientific: "Family (Scientific):",
      taxonomicOrder: "Taxonomic Order:",
    },
    observations: {
      loading: "Loading observations...",
      error: "Unable to load recent observations",
      noData: "No recent observations found for this species",
      tableHeaders: {
        location: "Location",
        date: "Date",
        count: "Count",
      },
    },
  },
  about: {
    title: "About",
    subtitle:
      "This is a React application built with React 19.2.0 and React Router v7.",
    description:
      "It demonstrates modern React functionality with the latest features and Material-UI for a polished user interface.",
    sections: {
      technologiesUsed: "Technologies Used:",
      features: "Features:",
    },
    technologies: [
      "React 19.2.0",
      "React Router v7",
      "Material-UI (MUI)",
      "React Hooks",
      "Functional Components",
      "Theme Context",
      "CSS-in-JS",
    ],
    features: [
      {
        title: "React Router v7",
        description: "Modern routing with Routes and element props",
      },
      {
        title: "React Hooks",
        description: "Functional components with useState and useEffect",
      },
      {
        title: "React 19",
        description: "Latest React version with createRoot API",
      },
      {
        title: "Material-UI",
        description: "Modern React UI framework with theme support",
      },
      {
        title: "Theme Toggle",
        description: "Light and dark theme switching functionality",
      },
      {
        title: "Responsive Design",
        description: "Mobile-friendly layout with Material-UI components",
      },
    ],
  },
};

export default Text;
