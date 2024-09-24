import { GridColDef } from "@mui/x-data-grid";
import Table from "../../components/Table/Table";
import { RedPandaSpecies } from "../../types/RedPanda";
import { useState } from "react";
import { Button, Grid2, Typography } from "@mui/material";
import CheckIcon from '@mui/icons-material/Check';
import AddIcon from '@mui/icons-material/Add';
import { redPandaColours } from "../../constants/panda.constants";
import { Sighting } from "../../types/Sighting";
import RPMap from "../../components/RPMap/RPMap";
import { Location } from "../../types/Location";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../constants/routes.constants";
import { MapService } from "../../service/MapService";

export default function SightingsList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);
  const [selectedLocation, setSelectedLocation] = useState<Location>();

  const navigate = useNavigate();

  const rows: Sighting[] = [
    {
      id: 'test0',
      dateTime: new Date().toISOString(),
      location: { lat: 28.3974, lon: 83.1258 },
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
      location: {lat: 28.0, lon: 84.1258},
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
      location: {lat: 27.3974, lon: 82.1258},
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
      location: {lat: 29.3974, lon: 85.1258},
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
        <Button onClick={() => setSelectedLocation(params.row.location)}>
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

  return (
    <Grid2 container spacing={4}>
      <Grid2 size={10}>
        <Typography variant="h6">Sightings</Typography>
      </Grid2>
      <Grid2 size={2}>
        <Button
          variant="contained"
          color="secondary"
          startIcon={<AddIcon />}
          onClick={() => navigate(Routes.addSighting)}
        >
          Sighting
        </Button>
      </Grid2>

    
      <Grid2 size={{ xs: 12, md: 8 }}>
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

      <Grid2 size={{ xs: 12, md: 4 }}>
        <RPMap 
          assets={selectedLocation ? [selectedLocation] : []}
          centerPoint={selectedLocation}
        />
      </Grid2>
    </Grid2>
  );
}
