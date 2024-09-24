import { useSnackbar } from "notistack";
import SightingForm from "../../components/Forms/SightingForm/SightingForm";
import { Routes } from "../../constants/routes.constants";
import { useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { RedPanda } from "../../types/RedPanda";
import { RedPandaService } from "../../service/RedPandaService";
import { SightingService } from "../../service/SightingsService";
import { AddSightingDTO } from "../../types/Sighting";

export default function AddSighting() {
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const [pandas, setPandas] = useState<RedPanda[]>([]);

  useEffect(() => {
    fetchPandas();
  }, []);
  
  const fetchPandas = async () => {
    const pandas = await RedPandaService.fetchPandas();
    setPandas(pandas);
  }

  const handleSave = async (sighting: AddSightingDTO) => {
    const response = await SightingService.addSighting(sighting);
    
    if (response) {
      enqueueSnackbar("Sighting successfully created.", { variant: "success" });
      navigate(Routes.sightings);
    } else {
      enqueueSnackbar("An error occurred while creating this sighting. Please try again.", { variant: "error" });
    }
  }

  const handleSavePanda = async (panda: RedPanda) => {
    const response = await RedPandaService.addPanda(panda);
    
    if (response) {
      enqueueSnackbar("Red panda successfully created.", { variant: "success" });
      fetchPandas();
    } else {
      enqueueSnackbar("An error occurred while creating this red panda. Please try again.", { variant: "error" });
    }
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
