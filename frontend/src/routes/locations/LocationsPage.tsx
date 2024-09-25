import { Box, Typography } from "@mui/material";
import RPMap from "../../components/RPMap/RPMap";
import { getTheme } from "../../themes/theme.helper";
import { defaultTheme } from "../../constants/theme.constants";
import { SightingService } from "../../service/SightingsService";
import { useEffect, useState } from "react";
import { Location } from "../../types/Location";

export default function LocationsPage() {
  const [locations, setLocations] = useState<Location[]>([]);

  useEffect(() => {
    fetchLocations();
  }, []);

  const fetchLocations = async () => {
    const locations = await SightingService.fetchLocations();
    setLocations(locations);
  }

  return (
    <Box sx={{ display: 'flex', flexDirection: "column", width: '100%', height: '100%'}}>
      <Typography variant="h6" sx={{ paddingBottom: getTheme(defaultTheme).spacing(4) }}>
        All sightings
      </Typography>

      <RPMap assets={locations} />
    </Box>
  );
}
