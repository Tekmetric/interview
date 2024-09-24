import { Box, Typography } from "@mui/material";
import RPMap from "../../components/RPMap/RPMap";
import { getTheme } from "../../themes/theme.helper";
import { defaultTheme } from "../../constants/theme.constants";

export default function LocationsPage() {
  const locations = [
    { latitude: 28.3974, longitude: 82.358 },
    { latitude: 28.35344, longitude: 85.058 },
    { latitude: 28.5342374, longitude: 80.1258 },
    { latitude: 29.0974, longitude: 84.558 },
    { latitude: 28.974, longitude: 83.4258 },
    { latitude: 26.34, longitude: 86.1258 },
    { latitude: 30.3974, longitude: 88.1258 },
    { latitude: 29.498, longitude: 90.809 },
    { latitude: 27.374, longitude: 81.1258 },
    { latitude: 29.3974, longitude: 83.58 },
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
