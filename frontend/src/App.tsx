import { ThemeProvider, createTheme } from "@mui/material";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ErrorBoundary } from "react-error-boundary";
import { ErrorContainer } from "./components/error-container/ErrorContainer";
import { AuthenticationProvider } from "./contexts/authenticationContext";
import { PostsPage } from "./pages/PostsPage";

const queryClient = new QueryClient();

const theme = createTheme();

function App() {
  return (
    <ErrorBoundary FallbackComponent={ErrorContainer}>
      <QueryClientProvider client={queryClient}>
        <ThemeProvider theme={theme}>
          <AuthenticationProvider
            value={{
              // for demo purposes, we are using a hardcoded user
              user: {
                id: "1",
                username: "John Doe",
                avatarFile: "avatar-1",
              },
            }}
          >
            <PostsPage />
          </AuthenticationProvider>
        </ThemeProvider>
      </QueryClientProvider>
    </ErrorBoundary>
  );
}

export default App;
