import { useNavigate } from "react-router-dom";
import { Box, Typography, Button, Container, useTheme } from "@mui/material";
import {
  Home as HomeIcon,
  ErrorOutline as ErrorIcon,
} from "@mui/icons-material";
import Text from "../../assets/Text";

const NotFound = () => {
  const navigate = useNavigate();
  const theme = useTheme();

  const handleGoHome = () => {
    navigate("/");
  };

  return (
    <Container maxWidth="md">
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "60vh",
          textAlign: "center",
          py: 4,
        }}
      >
        {/* Error Icon */}
        <Box
          sx={{
            mb: 4,
            p: 3,
            borderRadius: "50%",
            backgroundColor: theme.palette.error.light,
            color: theme.palette.error.contrastText,
          }}
        >
          <ErrorIcon sx={{ fontSize: 80 }} />
        </Box>

        {/* Main Content */}
        <Typography
          variant="h2"
          component="h1"
          gutterBottom
          sx={{
            fontWeight: 700,
            color: theme.palette.text.primary,
            mb: 2,
          }}
        >
          {Text.notFound.title}
        </Typography>

        <Typography
          variant="h5"
          component="h2"
          gutterBottom
          sx={{
            color: theme.palette.text.secondary,
            mb: 3,
            fontWeight: 400,
          }}
        >
          {Text.notFound.subtitle}
        </Typography>

        <Typography
          variant="body1"
          sx={{
            color: theme.palette.text.secondary,
            mb: 4,
            maxWidth: 600,
            lineHeight: 1.6,
          }}
        >
          {Text.notFound.description}
        </Typography>

        {/* Action Buttons */}
        <Box
          sx={{
            display: "flex",
            gap: 2,
            mb: 4,
            flexDirection: { xs: "column", sm: "row" },
            width: { xs: "100%", sm: "auto" },
          }}
        >
          <Button
            variant="contained"
            size="large"
            startIcon={<HomeIcon />}
            onClick={handleGoHome}
            sx={{
              px: 4,
              py: 1.5,
              fontSize: "1.1rem",
            }}
          >
            {Text.notFound.homeButton}
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default NotFound;
