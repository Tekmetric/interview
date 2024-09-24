import { useState } from "react";
import { Button, Grid2, Typography } from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import { useNavigate } from "react-router-dom";
import { pandaMock } from "../../../service/RedPandaService";
import { Routes } from "../../../constants/routes.constants";
import { getColumns } from "./RedPandaList.helper";
import { RedPanda } from "../../../types/RedPanda";
import Table from "../../../components/Table/Table";

export default function RedPandaList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);
  const [rows, setRows] = useState<RedPanda[]>(pandaMock);

  const navigate = useNavigate();

  const handleDelete = (pandaId: string) => { }

  const columns = getColumns(navigate, handleDelete);
 
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
