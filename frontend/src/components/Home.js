import React from "react";
import {
  Typography,
  Paper,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Link,
  Box,
} from "@mui/material";
import { CheckCircle as CheckIcon } from "@mui/icons-material";

const Home = () => {
  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom>
        Home Page
      </Typography>

      <Typography variant="h6" paragraph>
        Welcome to the interview app!
      </Typography>

      <Typography variant="body1" paragraph>
        This is the home page. Use the navigation above to explore different
        sections. The app now features Material-UI components with light/dark
        theme support!
      </Typography>

      <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
        <Typography variant="h5" component="h3" gutterBottom>
          Project Requirements:
        </Typography>

        <List>
          <ListItem>
            <ListItemIcon>
              <CheckIcon color="primary" />
            </ListItemIcon>
            <ListItemText
              primary="Fetch Data from a public API"
              secondary={
                <Link
                  href="https://github.com/toddmotto/public-apis"
                  target="_blank"
                  rel="noopener noreferrer"
                  color="primary"
                >
                  View API Samples
                </Link>
              }
            />
          </ListItem>

          <ListItem>
            <ListItemIcon>
              <CheckIcon color="primary" />
            </ListItemIcon>
            <ListItemText primary="Display data from API onto your page (Table, List, etc.)" />
          </ListItem>

          <ListItem>
            <ListItemIcon>
              <CheckIcon color="primary" />
            </ListItemIcon>
            <ListItemText
              primary="Apply a styling solution of your choice to make your page look different"
              secondary="Using Material-UI (MUI) with CSS-in-JS and theme support"
            />
          </ListItem>
        </List>
      </Paper>
    </Box>
  );
};

export default Home;
