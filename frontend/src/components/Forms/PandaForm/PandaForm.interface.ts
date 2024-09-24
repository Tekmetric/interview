import { RedPanda } from "../../../types/RedPanda";

export interface IPandaFormProps {
  panda?: RedPanda;
  onSave: (panda: RedPanda) => void;
  onDiscard: () => void;
}
