import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import ProductsGrid from "./pages/ProductsGrid";

export default function App() {
  return (
    <Container maxWidth="xl" className="py-8">
      <Typography variant="h4" className="font-bold mb-6 text-gray-800">
        🛍️ Product Dashboard
      </Typography>
      <ProductsGrid />
    </Container>
  );
}
