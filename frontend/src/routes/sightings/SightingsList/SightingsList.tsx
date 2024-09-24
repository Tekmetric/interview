import Table from "../../../components/Table/Table";
import { useState } from "react";
import { Button, Grid2, Typography } from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import { Sighting } from "../../../types/Sighting";
import RPMap from "../../../components/RPMap/RPMap";
import { Location } from "../../../types/Location";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../../constants/routes.constants";
import { getColumns } from "./SightingsList.helper";
import { sightingsMock } from "../../../service/SightingsService";

export default function SightingsList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);
  const [selectedLocation, setSelectedLocation] = useState<Location>();
  const [rows, setRows] = useState<Sighting[]>(sightingsMock);

  const navigate = useNavigate();

  const columns = getColumns(navigate, setSelectedLocation);

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
