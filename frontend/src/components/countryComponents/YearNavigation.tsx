import React from "react";
import { Box, Button } from "@mui/material";

interface YearNavigationProps {
  year: number;
  setYear: Function;
}

export const YearNavigation: React.FC<YearNavigationProps> = ({ year, setYear }) => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      <Button
        variant="contained"
        onClick={() => setYear(year - 1)}
      >{`Previous Year`}</Button>
      <Button
        variant="contained"
        onClick={() => setYear(year + 1)}
        style={{ marginLeft: '10px'}}
      >{`Next Year`}</Button>
    </Box>
  );
};
