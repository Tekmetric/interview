import { useNavigate, useParams } from "react-router-dom";
import PandaForm from "../../components/Forms/PandaForm/PandaForm";
import { useEffect, useState } from "react";
import { RedPanda } from "../../types/RedPanda";
import { pandaMock } from "../../service/RedPandaService";
import { Routes } from "../../constants/routes.constants";
import { useSnackbar } from "notistack";

export default function EditPanda() {
  const { id }  = useParams();
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const [panda, setPanda] = useState<RedPanda>();

  useEffect(() => {
    setPanda(pandaMock.find(panda => panda.id === id));
  }, [id]);

  const handleSave = () => {
    enqueueSnackbar("Red panda successfully created.", { variant: "success" });
    navigate(Routes.pandas);
  }

  const handleDiscard = () => {
    navigate(Routes.pandas);
  }

  return (
    <PandaForm panda={panda} onSave={handleSave} onDiscard={handleDiscard} />
  );
}
