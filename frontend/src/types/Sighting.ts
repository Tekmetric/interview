import { RedPanda } from "./RedPanda";
import { Location } from "./Location";

export type Sighting = {
  dateTime: string;
  location: Location;
  panda: RedPanda;
}
