import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { PostsPage } from "./pages/PostsPage";
import { ThemeProvider, createTheme } from "@mui/material";

const queryClient = new QueryClient();

const theme = createTheme();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <PostsPage />
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
