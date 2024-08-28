import React from "react";
import styled from "@emotion/styled";
import { Box, Button, TextField, Typography } from "@mui/material";
import { getCurrentDatetimeLocal } from "../utils/datetime";

const FilterContainer = styled(Box)`
  width: 100%;
  display: flex;
  gap: 10px;
  margin-top: 20px;
  align-items: center;
  justify-content: center;
`;

type FiltersProps = {
  filterStartDate: string | undefined;
  setFilterStartDate: (filterStartDate: string) => void;
  filterEndDate: string | undefined;
  setFilterEndDate: (filterEndDate: string) => void;
  handleFilterChange: () => void;
};

export default function Filters({
  filterStartDate,
  setFilterStartDate,
  filterEndDate,
  setFilterEndDate,
  handleFilterChange,
}: FiltersProps) {
  return (
    <FilterContainer>
      <TextField
        id="filterStartDate"
        name="filterStartDate"
        type="datetime-local"
        label="Filter Start Date"
        inputProps={{ min: getCurrentDatetimeLocal() }}
        InputLabelProps={{ shrink: true }}
        value={filterStartDate}
        onChange={(e) => setFilterStartDate(e.target.value)}
        required
      />
      <TextField
        id="filterEndDate"
        name="filterEndDate"
        type="datetime-local"
        label="Filter End Date"
        inputProps={{ min: filterStartDate || getCurrentDatetimeLocal() }}
        InputLabelProps={{ shrink: true }}
        value={filterEndDate}
        onChange={(e) => setFilterEndDate(e.target.value)}
        required
      />
      <Button
        variant="contained"
        onClick={handleFilterChange}
        color="secondary"
        disabled={
          !!filterStartDate &&
          !!filterEndDate &&
          filterStartDate > filterEndDate
        }
      >
        <Typography variant="button" style={{ color: "white" }}>
          Apply
        </Typography>
      </Button>
    </FilterContainer>
  );
}
