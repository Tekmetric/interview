import { useSnackbar } from "notistack";
import SightingForm from "../../components/Forms/SightingForm/SightingForm";
import { Routes } from "../../constants/routes.constants";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { RedPanda } from "../../types/RedPanda";

export default function AddSighting() {
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const [pandas, setPandas] = useState<RedPanda[]>([]);

  const handleSave = () => {
    enqueueSnackbar("Sighting successfully created.", { variant: "success" });
    navigate(Routes.sightings);
  }

  const handleSavePanda = () => {
    enqueueSnackbar("Panda successfully created. You can now find it listed.", { variant: "success" });
  }

  const handleDiscard = () => {
    navigate(Routes.sightings);
  }

  return <SightingForm
    onSave={handleSave}
    pandas={pandas}
    onDiscard={handleDiscard}
    onSavePanda={handleSavePanda}
  />;
}
