import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Typography,
  Paper,
  Button,
  Chip,
  Grid,
  Divider,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material";
import {
  ArrowBack as BackIcon,
  Info as InfoIcon,
  Visibility as ObservationsIcon,
} from "@mui/icons-material";
import {
  useGetTaxonomyQuery,
  useGetRecentSpeciesObservationsQuery,
} from "../../store/api/eBirdApi";
import Text from "../../assets/Text";

const SpeciesDetail = () => {
  const { speciesCode } = useParams();
  const navigate = useNavigate();
  const [speciesData, setSpeciesData] = useState(null);

  // Fetch taxonomy data to find the specific species
  const { data: taxonomyData, isLoading: taxonomyLoading } =
    useGetTaxonomyQuery();

  // Fetch recent observations for this species
  const {
    data: observationsData,
    isLoading: observationsLoading,
    error: observationsError,
  } = useGetRecentSpeciesObservationsQuery(
    { speciesCode, regionCode: "US", back: 30, maxResults: 10 },
    { skip: !speciesCode }
  );

  useEffect(() => {
    if (taxonomyData && speciesCode) {
      const species = taxonomyData.find((s) => s.speciesCode === speciesCode);
      setSpeciesData(species);
    }
  }, [taxonomyData, speciesCode]);

  const handleGoBack = () => {
    navigate("/species");
  };

  if (taxonomyLoading) {
    return (
      <Box>
        <Box display="flex" justifyContent="center" my={4}>
          <CircularProgress />
        </Box>
        <Typography variant="body1" textAlign="center">
          {Text.speciesDetail.loading}
        </Typography>
      </Box>
    );
  }

  if (!speciesData) {
    return (
      <Box>
        <Button startIcon={<BackIcon />} onClick={handleGoBack} sx={{ mb: 2 }}>
          {Text.speciesDetail.backButton}
        </Button>
        <Alert severity="error">
          {Text.speciesDetail.notFound.title} "{speciesCode}"{" "}
          {Text.speciesDetail.notFound.message}
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      {/* Header with Back Button */}
      <Box sx={{ mb: 3 }}>
        <Button startIcon={<BackIcon />} onClick={handleGoBack} sx={{ mb: 2 }}>
          {Text.speciesDetail.backButton}
        </Button>

        <Typography variant="h3" component="h1" gutterBottom>
          {speciesData.comName}
        </Typography>

        <Typography
          variant="h5"
          component="h2"
          fontStyle="italic"
          color="text.secondary"
        >
          {speciesData.sciName}
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Species Information */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
              <InfoIcon color="primary" sx={{ mr: 1 }} />
              <Typography variant="h6">
                {Text.speciesDetail.sections.speciesInformation}
              </Typography>
            </Box>

            <Divider sx={{ mb: 2 }} />

            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  {Text.speciesDetail.fields.category}
                </Typography>
                <Chip
                  label={speciesData.category}
                  color="primary"
                  variant="outlined"
                  size="small"
                  sx={{ mt: 0.5 }}
                />
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">
                  {Text.speciesDetail.fields.speciesCode}
                </Typography>
                <Typography variant="body1" fontFamily="monospace">
                  {speciesData.speciesCode}
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary">
                  {Text.speciesDetail.fields.order}
                </Typography>
                <Typography variant="body1">{speciesData.order}</Typography>
              </Grid>

              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary">
                  {Text.speciesDetail.fields.familyCommon}
                </Typography>
                <Typography variant="body1">
                  {speciesData.familyComName}
                </Typography>
              </Grid>

              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary">
                  {Text.speciesDetail.fields.familyScientific}
                </Typography>
                <Typography variant="body1" fontStyle="italic">
                  {speciesData.familySciName}
                </Typography>
              </Grid>

              {speciesData.taxonOrder && (
                <Grid item xs={12}>
                  <Typography variant="body2" color="text.secondary">
                    {Text.speciesDetail.fields.taxonomicOrder}
                  </Typography>
                  <Typography variant="body1">
                    {speciesData.taxonOrder}
                  </Typography>
                </Grid>
              )}
            </Grid>
          </Paper>
        </Grid>

        {/* Recent Observations */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
              <ObservationsIcon color="primary" sx={{ mr: 1 }} />
              <Typography variant="h6">
                {Text.speciesDetail.sections.recentObservations}
              </Typography>
            </Box>

            <Divider sx={{ mb: 2 }} />

            {observationsLoading ? (
              <Box display="flex" justifyContent="center" py={4}>
                <CircularProgress size={24} />
              </Box>
            ) : observationsError ? (
              <Alert severity="warning" size="small">
                {Text.speciesDetail.observations.error}
              </Alert>
            ) : observationsData && observationsData.length > 0 ? (
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        {Text.speciesDetail.observations.tableHeaders.location}
                      </TableCell>
                      <TableCell>
                        {Text.speciesDetail.observations.tableHeaders.date}
                      </TableCell>
                      <TableCell align="right">
                        {Text.speciesDetail.observations.tableHeaders.count}
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {observationsData.slice(0, 5).map((obs, index) => (
                      <TableRow key={index}>
                        <TableCell>
                          <Typography variant="body2" noWrap>
                            {obs.locName}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="body2">{obs.obsDt}</Typography>
                        </TableCell>
                        <TableCell align="right">
                          <Typography variant="body2">
                            {obs.howMany || "X"}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <Typography
                variant="body2"
                color="text.secondary"
                textAlign="center"
                py={4}
              >
                {Text.speciesDetail.observations.noData}
              </Typography>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default SpeciesDetail;
