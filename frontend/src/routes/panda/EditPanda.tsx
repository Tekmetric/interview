import { useNavigate, useParams } from "react-router-dom";
import PandaForm from "../../components/Forms/PandaForm/PandaForm";
import { useEffect, useState } from "react";
import { RedPanda } from "../../types/RedPanda";
import { RedPandaService } from "../../service/RedPandaService";
import { Routes } from "../../constants/routes.constants";
import { useSnackbar } from "notistack";

export default function EditPanda() {
  const { id }  = useParams();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const [panda, setPanda] = useState<RedPanda>();

  useEffect(() => {
    getPandaById();
  }, [id]);

  
  const getPandaById = async () => {
    if (!id) {
      return;
    }

    const panda = await RedPandaService.getById(id);
    setPanda(panda);
  }

  const handleSave = async (panda: RedPanda) => {
    const response = await RedPandaService.editPanda(panda);
    
    if (response) {
      enqueueSnackbar("Red panda successfully updated.", { variant: "success" });
      navigate(Routes.pandas);
    } else {
      enqueueSnackbar("An error occurred while updating this red panda. Please try again.", { variant: "error" });
    }
  }

  const handleDiscard = () => {
    navigate(Routes.pandas);
  }

  return (
    <PandaForm panda={panda} onSave={handleSave} onDiscard={handleDiscard} />
  );
}
