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
import Text from "../../assets/Text";

const Home = () => {
  return (
    <Box>
      <Typography variant="h3" component="h4" gutterBottom>
        {Text.home.title}
      </Typography>

      <Typography variant="h6">{Text.home.subtitle}</Typography>

      <Typography variant="body1">{Text.home.description}</Typography>

      <Paper elevation={3} sx={{ p: 3, mt: 3 }}>
        <Typography variant="h5" component="h3" gutterBottom>
          {Text.home.projectRequirementsTitle}
        </Typography>

        <List>
          <ListItem>
            <ListItemIcon>
              <CheckIcon color="primary" />
            </ListItemIcon>
            <ListItemText
              primary={Text.home.requirements.fetchData.primary}
              secondary={
                <Link
                  href={Text.home.requirements.fetchData.linkUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  color="primary"
                >
                  {Text.home.requirements.fetchData.linkText}
                </Link>
              }
            />
          </ListItem>

          <ListItem>
            <ListItemIcon>
              <CheckIcon color="primary" />
            </ListItemIcon>
            <ListItemText
              primary={Text.home.requirements.displayData.primary}
            />
          </ListItem>

          <ListItem>
            <ListItemIcon>
              <CheckIcon color="primary" />
            </ListItemIcon>
            <ListItemText
              primary={Text.home.requirements.styling.primary}
              secondary={Text.home.requirements.styling.secondary}
            />
          </ListItem>
        </List>
      </Paper>
    </Box>
  );
};

export default Home;
