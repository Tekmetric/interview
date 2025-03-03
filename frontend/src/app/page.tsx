import PortfolioBuilder from "@/features/portfolio-builder";
import { Container, Stack, Typography, Box } from "@mui/material";

export default function Home() {
  return (
    <Container
      sx={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        padding: 3,
        maxWidth: "100%",
      }}
    >
      <Stack spacing={2} alignItems="center">
        <Typography variant="h3" component="h1" align="center" gutterBottom>
          Portfolio Builder
        </Typography>
        <PortfolioBuilder />
      </Stack>
      <Box
        component="footer"
        sx={{ mt: 4, display: "flex", justifyContent: "center", overflowX: "hidden" }} // Prevent horizontal scrolling
      >
        Footer
      </Box>
    </Container>
  );
}
