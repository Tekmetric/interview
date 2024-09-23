import { Box, Typography } from "@mui/material";
import RPMap from "../../components/RPMap/RPMap";
import { getTheme } from "../../themes/theme.helper";
import { defaultTheme } from "../../constants/theme.constants";

export default function LocationsPage() {
  const locations = [
    { lat: 28.3974, lon: 82.358 },
    { lat: 28.35344, lon: 85.058 },
    { lat: 28.5342374, lon: 80.1258 },
    { lat: 29.0974, lon: 84.558 },
    { lat: 28.974, lon: 83.4258 },
    { lat: 26.34, lon: 86.1258 },
    { lat: 30.3974, lon: 88.1258 },
    { lat: 29.498, lon: 90.809 },
    { lat: 27.374, lon: 81.1258 },
    { lat: 29.3974, lon: 83.58 },
  ];

  return (
    <Box sx={{ display: 'flex', flexDirection: "column", width: '100%', height: '100%'}}>
      <Typography variant="h6" sx={{ paddingBottom: getTheme(defaultTheme).spacing(4) }}>
        All sightings
      </Typography>

      <RPMap assets={locations} />
    </Box>
  );
}
