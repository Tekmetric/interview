import { useParams } from "react-router-dom";
import PandaForm from "../../components/Forms/PandaForm/PandaForm";
import { useEffect, useState } from "react";
import { RedPanda } from "../../types/RedPanda";
import { pandaMock } from "../../service/RedPandaService";
import { Box, Grid2 } from "@mui/material";
import RedPandaImg from "../../assets/panda-bamboo.png";

export default function EditPanda() {
  const { id }  = useParams();
  const [panda, setPanda] = useState<RedPanda>();

  useEffect(() => {
    setPanda(pandaMock.find(panda => panda.id === id));
  }, [id]);

  console.log(id, panda);

  return (
    <PandaForm panda={panda} onSave={(panda) => setPanda(panda)}/>
  );
}
