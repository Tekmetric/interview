import { useSnackbar } from "notistack";
import PandaForm from "../../components/Forms/PandaForm/PandaForm";
import { useNavigate } from "react-router-dom";
import { Routes } from "../../constants/routes.constants";

export default function AddPanda() {
  const { enqueueSnackbar } = useSnackbar();
  const navigate = useNavigate();

  const handleSave = () => {
    enqueueSnackbar("Red panda successfully created.", { variant: "success" });
    navigate(Routes.pandas);
  }

  return (
    <PandaForm onSave={handleSave} />
  );
}
