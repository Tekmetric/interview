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
import Text from "../../assets/Text";

const About = () => {
  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom>
        {Text.about.title}
      </Typography>

      <Typography variant="h6">{Text.about.subtitle}</Typography>

      <Typography variant="body1">{Text.about.description}</Typography>

      <Paper elevation={3} sx={{ p: 3, mt: 3, mb: 3 }}>
        <Typography variant="h5" component="h3" gutterBottom>
          {Text.about.sections.technologiesUsed}
        </Typography>

        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 1 }}>
          {Text.about.technologies.map((tech, index) => (
            <Chip key={index} label={tech} color="primary" variant="outlined" />
          ))}
        </Box>
      </Paper>

      <Typography variant="h5" component="h3" gutterBottom>
        {Text.about.sections.features}
      </Typography>

      <Grid container spacing={2}>
        {Text.about.features.map((feature, index) => (
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
