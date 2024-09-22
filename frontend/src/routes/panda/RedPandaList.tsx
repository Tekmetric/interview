import { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import Table from "../../components/Table/Table";
import { RedPanda, RedPandaSpecies } from "../../types/RedPanda";
import { useState } from "react";
import { Box, Typography } from "@mui/material";
import CheckIcon from '@mui/icons-material/Check';
import { redPandaColours } from "../../constants/panda.constants";

export default function RedPandaList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);

  const rows: RedPanda[] = [
    {
      id: 'test0',
      age: 10,
      colour: redPandaColours[0],
      hasTracker: true,
      name: "Kylo",
      species: RedPandaSpecies.Himalayan
    },
    {
      id: 'test1',
      age: 3,
      colour: redPandaColours[1],
      hasTracker: false,
      name: "Snitzel",
      species: RedPandaSpecies.Chinese
    },
    {
      id: 'test2',
      age: 3,
      colour: redPandaColours[2],
      hasTracker: true,
      name: "Tofu",
      species: RedPandaSpecies.Chinese
    },
    {
      id: 'test3',
      age: 4,
      colour: redPandaColours[3],
      hasTracker: false,
      name: "Pixel",
      species: RedPandaSpecies.Chinese
    }
  ];

  const columns: GridColDef[] = [
    { field: 'name', headerName: 'Name', minWidth: 160 },
    {
      field: 'age',
      headerName: 'Age',
      type: 'number',
      minWidth: 90,
    },
    {
      field: 'species',
      headerName: 'Species',
      sortable: false,
      minWidth: 160,
      valueGetter: (_, row: RedPanda) => row.species === RedPandaSpecies.Chinese ? "Chinese" : "Himalayan",
    },
    {
      field: 'hastracker',
      headerName: 'Has tracker',
      sortable: false,
      type: "custom",
      minWidth: 90,
      renderCell: (params) => params.row.hasTracker  ? <CheckIcon /> : "",
    },
    {
      field: 'colour',
      headerName: 'Colour',
      sortable: false,
      type: "custom",
      minWidth: 90,
      renderCell: (params: GridRenderCellParams) => <Box sx={{ width: '100%', height: "100%", background: params.row.colour }} />
    },
  ];

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%', height: '100%'}}>
      <Typography variant="h6">Red pandas</Typography>
    
      <Table
        rows={rows}
        columns={columns}
        paginationModel={{ page: page, pageSize: pageSize }}
        onPaginationModelChange={(model) => {
          setPage(model.page);
          setPageSize(model.pageSize);
        }}
      />
    </Box>
  );
}
