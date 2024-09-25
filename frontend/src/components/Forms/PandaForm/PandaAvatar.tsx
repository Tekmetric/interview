import React from 'react';
import { Box } from "@mui/material";
import { IPandaAvatarProps } from "./PandaForm.interface";
import { RedPandaSpecies } from "../../../types/RedPanda";
import ChinesePanda from "../../../assets/redpanda-landing.png";
import HimalayanPanda from "../../../assets/redpanda-landing-himalayan.png";
import Tracker from "../../../assets/tracker.png";

export default function PandaAvatar({ hasTracker, species }: IPandaAvatarProps) {
  return (
    <Box sx={{ display: "flex", alignItems: "center", justifyContent: "center", flexGrow: 1, height: "100%" }}>
      <Box sx={{ position: "relative" }}>
        <img src={species === RedPandaSpecies.Chinese ? ChinesePanda : HimalayanPanda} height={300} />
        {hasTracker && (
          <Box sx={{ position: "absolute", top: 120, left: 130 }}>
            <img src={Tracker} height={24} />
          </Box>
        )}
      </Box>
    </Box>
  )
};
