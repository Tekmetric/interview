import { RedPanda } from "../../../types/RedPanda";
import { SightingDTO } from "../../../types/Sighting";

export interface ISightingFormProps {
  pandas: RedPanda[];
  onSave: (sighting: SightingDTO) => void;
}
