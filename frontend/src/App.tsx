import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { PostsPage } from "./pages/PostsPage";
import { ThemeProvider, createTheme } from "@mui/material";
import { AuthenticationProvider } from "./contexts/authenticationContext";

const queryClient = new QueryClient();

const theme = createTheme();

function App() {
  return (
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
  );
}

export default App;
