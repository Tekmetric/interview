import { RedPanda } from "./RedPanda";
import { Location } from "./Location";

export type Sighting = {
  id: string;
  dateTime: string;
  location: Location;
  panda: RedPanda;
}

export type AddSightingDTO = {
  dateTime: string | undefined;
  location: Location | undefined;
  pandaId: string;
}

export type SightingDTO = {
  id: string;
  dateTime: string | undefined;
  locationLat: number | null;
  locationLon: number | null;
  pandaId: string;
}

