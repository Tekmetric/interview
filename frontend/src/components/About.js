import React from "react";
import {
  Typography,
  Paper,
  Chip,
  Box,
  Grid,
  Card,
  CardContent,
} from "@mui/material";

const About = () => {
  const technologies = [
    "React 19.2.0",
    "React Router v7",
    "Material-UI (MUI)",
    "React Hooks",
    "Functional Components",
    "Theme Context",
    "CSS-in-JS",
  ];

  const features = [
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
  ];

  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom>
        About
      </Typography>

      <Typography variant="h6" paragraph>
        This is a React application built with React 19.2.0 and React Router v7.
      </Typography>

      <Typography variant="body1" paragraph>
        It demonstrates modern React functionality with the latest features and
        Material-UI for a polished user interface.
      </Typography>

      <Paper elevation={3} sx={{ p: 3, mt: 3, mb: 3 }}>
        <Typography variant="h5" component="h3" gutterBottom>
          Technologies Used:
        </Typography>

        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
          {technologies.map((tech, index) => (
            <Chip key={index} label={tech} color="primary" variant="outlined" />
          ))}
        </Box>
      </Paper>

      <Typography variant="h5" component="h3" gutterBottom>
        Features:
      </Typography>

      <Grid container spacing={2}>
        {features.map((feature, index) => (
          <Grid item xs={12} sm={6} md={4} key={index}>
            <Card elevation={2} sx={{ height: "100%" }}>
              <CardContent>
                <Typography variant="h6" component="h4" gutterBottom>
                  {feature.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {feature.description}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
};

export default About;
