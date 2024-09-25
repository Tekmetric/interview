import React from "react";
import { useSnackbar } from "notistack";
import PandaForm from "../../components/Forms/PandaForm/PandaForm";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../constants/routes.constants";
import { RedPandaService } from "../../service/RedPandaService";
import { RedPanda } from "../../types/RedPanda";

const AddPanda: React.FC = () => {
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const handleSave = async (panda: RedPanda) => {
    const response = await RedPandaService.addPanda(panda);
    
    if (response) {
      enqueueSnackbar("Red panda successfully created.", { variant: "success" });
      navigate(Routes.pandas);
    } else {
      enqueueSnackbar("An error occurred while creating this red panda. Please try again.", { variant: "error" });
    }
  }

  const handleDiscard = () => {
    navigate(Routes.pandas);
  }

  return (
    <PandaForm onSave={handleSave} onDiscard={handleDiscard} />
  );
}

export default AddPanda;
