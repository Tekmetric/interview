import { RedPanda } from "./RedPanda";
import { Location } from "./Location";

export type Sighting = {
  id: string;
  dateTime: string;
  location: Location;
  panda: RedPanda;
}
