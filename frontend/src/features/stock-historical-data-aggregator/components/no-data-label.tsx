import { Box, Typography } from "@mui/material";

const NoDataLabel = () => {
  return (
    <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      height="100%"
    >
      <Typography variant="body2" color="textSecondary">
        No data for the selected range...
      </Typography>
    </Box>
  );
};
export default NoDataLabel;
