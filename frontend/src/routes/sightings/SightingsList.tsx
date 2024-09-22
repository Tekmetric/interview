import { GridColDef, GridRenderCellParams } from "@mui/x-data-grid";
import Table from "../../components/Table/Table";
import { RedPanda, RedPandaSpecies } from "../../types/RedPanda";
import { useState } from "react";
import { Box, Typography } from "@mui/material";
import CheckIcon from '@mui/icons-material/Check';
import { redPandaColours } from "../../constants/panda.constants";
import { Sighting } from "../../types/Sighting";

export default function SightingsList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);

  const rows: Sighting[] = [
    {
      id: 'test0',
      dateTime: new Date().toISOString(),
      location: {lat: "28.3974", lon: "84.1258"},
      panda: {
        id: 'test0',
        age: 10,
        colour: redPandaColours[0],
        hasTracker: true,
        name: "Kylo",
        species: RedPandaSpecies.Himalayan
      },
    },
    {
      id: 'test1',
      dateTime: new Date().toISOString(),
      location: {lat: "28.3974", lon: "84.1258"},
      panda: {
        id: 'test1',
        age: 3,
        colour: redPandaColours[1],
        hasTracker: false,
        name: "Snitzel",
        species: RedPandaSpecies.Chinese
        }
    },
    {
      id: 'test2',
      dateTime: new Date().toISOString(),
      location: {lat: "28.3974", lon: "84.1258"},
      panda: {
        id: 'test2',
        age: 3,
        colour: redPandaColours[2],
        hasTracker: true,
        name: "Tofu",
        species: RedPandaSpecies.Chinese
      }
    },
    {
      id: 'test3',
      dateTime: new Date().toISOString(),
      location: {lat: "28.3974", lon: "84.1258"},
      panda: {
        id: 'test3',
        age: 4,
        colour: redPandaColours[3],
        hasTracker: false,
        name: "Pixel",
        species: RedPandaSpecies.Chinese
      }
    }
  ];

  const columns: GridColDef[] = [
    { 
      field: 'dateTime',
      headerName: 'Date time',
      minWidth: 200,
      type: "dateTime",
      valueGetter: (_, row: Sighting) => new Date(row.dateTime),
    },
    {
      field: 'location',
      headerName: 'Location',
      minWidth: 200,
      type: "custom",
      valueGetter: (_, row: Sighting) => `${row.location.lat}° N,  ${row.location.lon}° E`,
    },
    {
      field: 'name',
      headerName: 'Name',
      sortable: false,
      minWidth: 160,
      valueGetter: (_, row: Sighting) => row.panda.name,
      // TODO: redirect to panda detail page on click
    },
    {
      field: 'age',
      headerName: 'Age',
      minWidth: 90,
      type: "number",
      valueGetter: (_, row: Sighting) => row.panda.age,
    },
    {
      field: 'species',
      headerName: 'Species',
      sortable: false,
      minWidth: 160,
      valueGetter: (_, row: Sighting) => row.panda.species === RedPandaSpecies.Chinese ? "Chinese" : "Himalayan",
    },
    {
      field: 'hastracker',
      headerName: 'Has tracker',
      sortable: false,
      minWidth: 90,
      type: "custom",
      renderCell: (params) => params.row.panda.hasTracker  ? <CheckIcon /> : "",
    }
  ];

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%', height: '100%'}}>
      <Typography variant="h6">Sightings</Typography>
    
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
