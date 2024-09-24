import { Box, Button, IconButton } from "@mui/material";
import { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import { Routes } from "../../../constants/routes.constants";
import { RedPanda, RedPandaSpeciesLabels } from "../../../types/RedPanda";
import { NavigateFunction } from "react-router-dom";
import CheckIcon from '@mui/icons-material/Check';
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

export const getColumns = (
  navigate: NavigateFunction,
  handleDelete: (pandaId: string) => void
): GridColDef[] => [
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
    renderCell: (params: GridRenderCellParams) => (
      <Box sx={{ width: '100%', height: "100%", background: params.row.colour }} />
    )
  },
  {
    field: 'actions',
    headerName: 'Actions',
    sortable: false,
    type: "custom",
    width: 120,
    renderCell: (params: GridRenderCellParams) => (
      <>
        <IconButton color="info" onClick={() => navigate(`${Routes.pandas}/edit/${params.row.id}`)}>
          <EditIcon />
        </IconButton>
        <IconButton color="error" onClick={() => handleDelete(params.row.id)}>
          <DeleteIcon />
        </IconButton>
      </>
    )
  },
];
