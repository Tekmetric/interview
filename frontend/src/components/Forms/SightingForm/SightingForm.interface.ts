import { RedPanda } from "../../../types/RedPanda";
import { AddSightingDTO } from "../../../types/Sighting";

export interface ISightingFormProps {
  pandas: RedPanda[];
  onSave: (sighting: AddSightingDTO) => void;
  onSavePanda: (panda: RedPanda) => void;
  onDiscard: () => void;
}
