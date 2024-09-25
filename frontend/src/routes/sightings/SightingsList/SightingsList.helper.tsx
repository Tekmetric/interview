import { GridColDef } from "@mui/x-data-grid";
import { Sighting } from "../../../types/Sighting";
import { Button } from "@mui/material";
import { MapService } from "../../../service/MapService";
import { Routes } from "../../../constants/routes.constants";
import { RedPandaSpecies } from "../../../types/RedPanda";
import { NavigateFunction } from "react-router-dom";
import CheckIcon from '@mui/icons-material/Check';
import { Location } from "../../../types/Location";

export const getColumns = (
  navigate: NavigateFunction,
  onLocationChange: (value: Location | undefined) => void
): GridColDef[] => [
  { 
    field: 'dateTime',
    headerName: 'Date time',
    minWidth: 200,
    flex: 1,
    type: "dateTime",
    valueGetter: (_, row: Sighting) => new Date(row.dateTime),
  },
  {
    field: 'location',
    headerName: 'Location',
    minWidth: 200,
    flex: 1,
    type: "custom",
    renderCell: (params) => (
      <Button onClick={() => onLocationChange(params.row.location)}>
        {MapService.formatLocationForDisplay(params.row.location)}
      </Button>
    ),
  },
  {
    field: 'name',
    headerName: 'Name',
    sortable: false,
    minWidth: 130,
    flex: 1,
    renderCell: (params) => (
      <Button onClick={() => navigate(`${Routes.pandas}/${params.row.panda.id}`)}>
        {params.row.panda.name}
      </Button>
    ),
  },
  {
    field: 'age',
    headerName: 'Age',
    width: 90,
    type: "number",
    valueGetter: (_, row: Sighting) => row.panda.age,
  },
  {
    field: 'species',
    headerName: 'Species',
    sortable: false,
    minWidth: 130,
    flex: 1,
    valueGetter: (_, row: Sighting) => row.panda.species === RedPandaSpecies.Chinese ? "Chinese" : "Himalayan",
  },
  {
    field: 'hastracker',
    headerName: 'Has tracker',
    sortable: false,
    width: 120,
    type: "custom",
    renderCell: (params) => params.row.panda.hasTracker  ? <CheckIcon /> : "",
  }
];
