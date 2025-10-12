import { useState } from "react";
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  CircularProgress,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  Chip,
  Grid,
} from "@mui/material";
import {
  Refresh as RefreshIcon,
  LocationOn as LocationIcon,
  CalendarToday as CalendarIcon,
} from "@mui/icons-material";
import {
  useGetRecentObservationsQuery,
  useGetRecentNotableObservationsQuery,
  useGetSubRegionsQuery,
} from "../store/api/eBirdApi";
import Text from "../assets/Text";

const BirdData = () => {
  const [regionCode, setRegionCode] = useState("US-CA"); // California as default
  const [maxResults, setMaxResults] = useState(25);
  const [back, setBack] = useState(7);
  const [dataType, setDataType] = useState("recent");

  // API queries
  const {
    data: recentObservations,
    error: recentError,
    isLoading: recentLoading,
    refetch: refetchRecent,
  } = useGetRecentObservationsQuery(
    { regionCode, back, maxResults },
    { skip: dataType !== "recent" }
  );

  const {
    data: notableObservations,
    error: notableError,
    isLoading: notableLoading,
    refetch: refetchNotable,
  } = useGetRecentNotableObservationsQuery(
    { regionCode, back, maxResults },
    { skip: dataType !== "notable" }
  );

  const { data: subRegions, isLoading: regionsLoading } = useGetSubRegionsQuery(
    { regionType: "subnational1", parentRegionCode: "US" }
  );

  // Determine which data to display
  const currentData =
    dataType === "recent" ? recentObservations : notableObservations;
  const currentError = dataType === "recent" ? recentError : notableError;
  const currentLoading = dataType === "recent" ? recentLoading : notableLoading;

  const handleRefresh = () => {
    if (dataType === "recent") {
      refetchRecent();
    } else {
      refetchNotable();
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom>
        {Text.birdData.title}
      </Typography>

      <Typography variant="h6" gutterBottom>
        {Text.birdData.subtitle}
      </Typography>

      {/* Controls */}
      <Paper elevation={2} sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth>
              <InputLabel>{Text.birdData.controls.dataType}</InputLabel>
              <Select
                value={dataType}
                label={Text.birdData.controls.dataType}
                onChange={(e) => setDataType(e.target.value)}
              >
                <MenuItem value="recent">
                  {Text.birdData.dataTypes.recent}
                </MenuItem>
                <MenuItem value="notable">
                  {Text.birdData.dataTypes.notable}
                </MenuItem>
              </Select>
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth>
              <InputLabel>{Text.birdData.controls.region}</InputLabel>
              <Select
                value={regionCode}
                label={Text.birdData.controls.region}
                onChange={(e) => setRegionCode(e.target.value)}
                disabled={regionsLoading}
              >
                <MenuItem value="US">United States</MenuItem>
                <MenuItem value="US-CA">California</MenuItem>
                <MenuItem value="US-NY">New York</MenuItem>
                <MenuItem value="US-FL">Florida</MenuItem>
                <MenuItem value="US-TX">Texas</MenuItem>
                {subRegions?.map((region) => (
                  <MenuItem key={region.code} value={region.code}>
                    {region.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <TextField
              fullWidth
              label={Text.birdData.controls.daysBack}
              type="number"
              value={back}
              onChange={(e) =>
                setBack(
                  Math.max(1, Math.min(30, parseInt(e.target.value) || 7))
                )
              }
              slotProps={{ htmlInput: { min: 1, max: 30 } }}
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <TextField
              fullWidth
              label={Text.birdData.controls.maxResults}
              type="number"
              value={maxResults}
              onChange={(e) =>
                setMaxResults(
                  Math.max(1, Math.min(100, parseInt(e.target.value) || 25))
                )
              }
              slotProps={{ htmlInput: { min: 1, max: 100 } }}
            />
          </Grid>

          <Grid item xs={12} md={2}>
            <Button
              fullWidth
              variant="contained"
              onClick={handleRefresh}
              startIcon={<RefreshIcon />}
              disabled={currentLoading}
            >
              {Text.birdData.controls.refresh}
            </Button>
          </Grid>
        </Grid>
      </Paper>

      {/* Error Display */}
      {currentError && (
        <Alert severity="error" sx={{ mb: 3 }}>
          Error loading data:{" "}
          {currentError?.data?.message ||
            currentError?.message ||
            "Unknown error"}
          <br />
          <Typography variant="caption">{Text.birdData.apiKeyError}</Typography>
        </Alert>
      )}

      {/* Loading State */}
      {currentLoading && (
        <Box display="flex" justifyContent="center" my={4}>
          <CircularProgress />
        </Box>
      )}

      {/* Data Display */}
      {currentData && !currentLoading && (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>{Text.birdData.tableHeaders.species}</TableCell>
                <TableCell>
                  {Text.birdData.tableHeaders.scientificName}
                </TableCell>
                <TableCell>{Text.birdData.tableHeaders.location}</TableCell>
                <TableCell>{Text.birdData.tableHeaders.date}</TableCell>
                <TableCell>{Text.birdData.tableHeaders.count}</TableCell>
                <TableCell>{Text.birdData.tableHeaders.observer}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {currentData.map((observation, index) => (
                <TableRow key={`${observation.speciesCode}-${index}`}>
                  <TableCell>
                    <Box>
                      <Typography variant="body2" fontWeight="bold">
                        {observation.comName}
                      </Typography>
                      {observation.exotic && (
                        <Chip
                          label={Text.birdData.chips.exotic}
                          size="small"
                          color="warning"
                          sx={{ mt: 0.5 }}
                        />
                      )}
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2" fontStyle="italic">
                      {observation.sciName}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Box display="flex" alignItems="center" gap={0.5}>
                      <LocationIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {observation.locName}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Box display="flex" alignItems="center" gap={0.5}>
                      <CalendarIcon fontSize="small" color="action" />
                      <Typography variant="body2">
                        {formatDate(observation.obsDt)}
                      </Typography>
                    </Box>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2">
                      {observation.howMany || "X"}
                    </Typography>
                  </TableCell>
                  <TableCell>
                    <Typography variant="body2">
                      {observation.userDisplayName || "Anonymous"}
                    </Typography>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* No Data Message */}
      {currentData && currentData.length === 0 && !currentLoading && (
        <Paper sx={{ p: 3, textAlign: "center" }}>
          <Typography variant="h6" color="text.secondary">
            {Text.birdData.noDataMessage}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {Text.birdData.noDataSubtext}
          </Typography>
        </Paper>
      )}
    </Box>
  );
};

export default BirdData;
