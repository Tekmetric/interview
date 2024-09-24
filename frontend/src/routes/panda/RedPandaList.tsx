import { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import Table from "../../components/Table/Table";
import { RedPanda, RedPandaSpeciesLabels } from "../../types/RedPanda";
import { useState } from "react";
import { Box, Button, Grid2, IconButton, Typography } from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import CheckIcon from '@mui/icons-material/Check';
import { useNavigate } from "react-router-dom";
import { Routes } from "../../constants/routes.constants";
import { pandaMock } from "../../service/RedPandaService";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

export default function RedPandaList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);

  const navigate = useNavigate();

  const rows = pandaMock;

  const columns: GridColDef[] = [
    { 
      field: 'name',
      headerName: 'Name',
      flex: 1,
      renderCell: (params) => (
        <Button onClick={() => navigate(`${Routes.pandas}/${params.row.id}`)}>
          {params.row.name}
        </Button>
      ),
    },
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
      flex: 1,
      valueGetter: (_, row: RedPanda) => RedPandaSpeciesLabels[row.species],
    },
    {
      field: 'hastracker',
      headerName: 'Has tracker',
      sortable: false,
      type: "custom",
      width: 120,
      renderCell: (params) => params.row.hasTracker  ? <CheckIcon /> : "",
    },
    {
      field: 'colour',
      headerName: 'Colour',
      sortable: false,
      type: "custom",
      width: 90,
      renderCell: (params: GridRenderCellParams) => <Box sx={{ width: '100%', height: "100%", background: params.row.colour }} />
    },
    {
      field: 'actions',
      headerName: 'Actions',
      sortable: false,
      type: "custom",
      width: 120,
      renderCell: (params: GridRenderCellParams) => (
        <>
          <IconButton color="info" onClick={() => navigate(`${Routes.pandas}/edit/${params.row.id}`)}><EditIcon /></IconButton>
          <IconButton color="error" onClick={() => alert("TODO: delete")}><DeleteIcon /></IconButton>
        </>
      )
    },
  ];

  return (
    <Grid2 container spacing={4}>
      <Grid2 size={10}>
        <Typography variant="h6">Red pandas</Typography>
      </Grid2>
      <Grid2 size={2}>
        <Button
          variant="contained"
          color="secondary"
          startIcon={<AddIcon />}
          onClick={() => navigate(Routes.addPanda)}
        >
          Red panda
        </Button>
      </Grid2>
    
      <Grid2 size={12}>
        <Table
          rows={rows}
          columns={columns}
          paginationModel={{ page: page, pageSize: pageSize }}
          onPaginationModelChange={(model) => {
            setPage(model.page);
            setPageSize(model.pageSize);
          }}
        />
      </Grid2>
    </Grid2>
  );
}
