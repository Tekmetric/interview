import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
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
  TablePagination,
  CircularProgress,
  Alert,
  TextField,
  InputAdornment,
  Chip,
} from "@mui/material";
import {
  Search as SearchIcon,
  Visibility as ViewIcon,
} from "@mui/icons-material";
import { useGetTaxonomyQuery } from "../../store/api/eBirdApi";
import Text from "../../assets/Text";

const Species = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [rowsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState("");

  // Fetch taxonomy data
  const { data: taxonomyData, error, isLoading } = useGetTaxonomyQuery();

  // Filter and paginate data
  const filteredData = useMemo(() => {
    if (!taxonomyData) return [];

    return taxonomyData.filter(
      (species) =>
        species.comName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        species.sciName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        species.familyComName
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ||
        species.order?.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [taxonomyData, searchTerm]);

  const paginatedData = useMemo(() => {
    const startIndex = page * rowsPerPage;
    return filteredData.slice(startIndex, startIndex + rowsPerPage);
  }, [filteredData, page, rowsPerPage]);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleRowClick = (speciesCode) => {
    navigate(`/species/${speciesCode}`);
  };

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
    setPage(0); // Reset to first page when searching
  };

  if (isLoading) {
    return (
      <Box>
        <Typography variant="h3" component="h1" gutterBottom>
          {Text.species.title}
        </Typography>
        <Typography variant="h6" gutterBottom>
          {Text.species.subtitle}
        </Typography>
        <Box display="flex" justifyContent="center" my={4}>
          <CircularProgress />
        </Box>
        <Typography variant="body1" textAlign="center">
          {Text.species.loading}
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box>
        <Typography variant="h3" component="h1" gutterBottom>
          {Text.species.title}
        </Typography>
        <Alert severity="error" sx={{ mt: 2 }}>
          {Text.species.error}
        </Alert>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h3" component="h1" gutterBottom>
        {Text.species.title}
      </Typography>

      <Typography variant="h6" gutterBottom>
        {Text.species.subtitle}
      </Typography>

      {/* Search Field */}
      <Box sx={{ mb: 3 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder={Text.species.searchPlaceholder}
          value={searchTerm}
          onChange={handleSearchChange}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ maxWidth: 400 }}
        />
      </Box>

      {/* Results Summary */}
      <Box sx={{ mb: 2 }}>
        <Typography variant="body2" color="text.secondary">
          {Text.species.resultsText.showing} {paginatedData.length}{" "}
          {Text.species.resultsText.of} {filteredData.length}{" "}
          {Text.species.resultsText.species}
          {searchTerm &&
            ` ${Text.species.resultsText.matching} "${searchTerm}"`}
        </Typography>
      </Box>

      {/* Species Table */}
      <Paper elevation={3}>
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>{Text.species.tableHeaders.commonName}</TableCell>
                <TableCell>
                  {Text.species.tableHeaders.scientificName}
                </TableCell>
                <TableCell>{Text.species.tableHeaders.category}</TableCell>
                <TableCell>{Text.species.tableHeaders.order}</TableCell>
                <TableCell>{Text.species.tableHeaders.family}</TableCell>
                <TableCell align="center">
                  {Text.species.tableHeaders.actions}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {paginatedData.length > 0 ? (
                paginatedData.map((species) => (
                  <TableRow
                    key={species.speciesCode}
                    hover
                    onClick={() => handleRowClick(species.speciesCode)}
                    sx={{
                      cursor: "pointer",
                      "&:hover": {
                        backgroundColor: "action.hover",
                      },
                    }}
                  >
                    <TableCell>
                      <Typography variant="body2" fontWeight="bold">
                        {species.comName}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" fontStyle="italic">
                        {species.sciName}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={species.category}
                        size="small"
                        variant="outlined"
                        color="primary"
                      />
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">{species.order}</Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {species.familyComName}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <ViewIcon color="action" fontSize="small" />
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6} align="center">
                    <Typography variant="body1" color="text.secondary" py={4}>
                      {Text.species.noData}
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Pagination */}
        <TablePagination
          component="div"
          count={filteredData.length}
          page={page}
          onPageChange={handleChangePage}
          rowsPerPage={rowsPerPage}
          rowsPerPageOptions={[]}
          labelDisplayedRows={({ from, to, count }) =>
            `${from}-${to} ${Text.species.pagination.of} ${count}`
          }
        />
      </Paper>
    </Box>
  );
};

export default Species;
