import { useEffect, useState } from "react";
import { Button, Grid2, Typography } from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import { useNavigate } from "react-router-dom";
import { RedPandaService } from "../../../service/RedPandaService";
import { Routes } from "../../../constants/routes.constants";
import { getColumns } from "./RedPandaList.helper";
import { RedPanda } from "../../../types/RedPanda";
import Table from "../../../components/Table/Table";
import { enqueueSnackbar } from "notistack";
import ConfirmationDialog from "../../../components/ConfirmationDialog/ConfirmationDialog";

export default function RedPandaList() {
  const [page, setPage] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(5);
  const [rows, setRows] = useState<RedPanda[]>([]);

  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const [selectedPanda, setSelectedPanda] = useState<RedPanda>();

  const navigate = useNavigate();

  useEffect(() => {
    fetchPandas();
  }, []);

  const fetchPandas = async () => {
    const pandas = await RedPandaService.fetchPandas();
    setRows(pandas);
  }

  const deletePanda = async () => { 
    if (!selectedPanda) {
      return;
    }

    const response = await RedPandaService.deleteById(selectedPanda.id);

    if(response) {
      enqueueSnackbar("Red panda successfully deleted.", { variant: "success" });
      fetchPandas();
    } else {
      enqueueSnackbar("An error occurred while deleting this red panda. Please try again.", { variant: "error" });
    }
    
    setShowConfirmDelete(false);
  } 

  const handleDelete = (panda: RedPanda) => {
    setSelectedPanda(panda);
    setShowConfirmDelete(true);
  }

  const handleDiscardDelete = () => {
    setSelectedPanda(undefined);
    setShowConfirmDelete(false);
  }

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

      <ConfirmationDialog 
        open={showConfirmDelete && !!selectedPanda}
        message={`Are you sure you want to delete ${selectedPanda?.name}?`}
        onConfirm={deletePanda}
        onDiscard={handleDiscardDelete}
        title={"Warning"}
      />
    </Grid2>
  );
}
