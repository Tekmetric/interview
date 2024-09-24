import React from "react";
import { Box, Typography } from "@mui/material";
import PandaBinoculars from "../../assets/panda-binocular.png";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../constants/routes.constants";
import { PrimaryGradientButton, SecondaryGradientButton } from "./HomePage.styles";
import NavigateIcon from "@mui/icons-material/NavigateNext";

export default function HomePage() {
  const navigate = useNavigate();

  return (
    <Box sx={{ display: 'flex', justifyContent: "center", alignItems: "center", height: '100%', flexWrap: "wrap", padding: 3 }}>
      <img src={PandaBinoculars} height={248} />

      <Box sx={{ display: 'flex', justifyContent: "center", alignItems: "center", flexDirection: "column", margin: 6 }}>
        <Typography variant="h4">
          Welcome to Red Panda Tracker!
        </Typography>
        <Typography variant="h6">
          An app for logging your red panda sightings and monitoring pandas wearing a GPS tracker.
        </Typography>

        <Box sx={{ display: 'flex', justifyContent: "center", alignItems: "center", margin: 4 }}>
          <PrimaryGradientButton
            onClick={() => navigate(Routes.sightings)}
            startIcon={<NavigateIcon />}
            color="primary"
            variant="outlined"
            sx={{ mx: 2 }}
            >
            Get started
          </PrimaryGradientButton>

          <SecondaryGradientButton variant="contained" onClick={() => navigate(Routes.addSighting)}>
            I saw a panda!
          </SecondaryGradientButton>
        </Box>
      </Box>
    </Box>
  );
}
