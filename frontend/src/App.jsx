import { createRoot } from "react-dom/client";
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import ArtDetails from "./components/ArtDetails";
import ArtList from "./components/ArtList";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: Infinity,
      cacheTime: Infinity,
    },
  },
});

const App = () => {
  return (
    <div>
      <BrowserRouter>
        <QueryClientProvider client={queryClient}>
          <header className="text-center text-white text-5xl w-full p-10 bg-teal-600">
            <Link to="/">Rijksmuseum Virtual Tour</Link>
          </header>
          <Routes>
            <Route path="/" element={<ArtList />} />
            <Route path="/details/:id" element={<ArtDetails />} />
          </Routes>
        </QueryClientProvider>
      </BrowserRouter>
    </div>
  );
};

const container = document.getElementById("root");
const root = createRoot(container);
root.render(<App />);
