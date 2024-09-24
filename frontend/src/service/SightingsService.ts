import { createSighting, fetchAllSightings } from "../api/sightings.api";
import { Location } from "../types/Location";
import { RedPanda } from "../types/RedPanda";
import { Sighting, AddSightingDTO, SightingDTO } from "../types/Sighting";

const buildSighting = (
  pandaId: string | undefined,
  location: Location | undefined,
  dateTime: string | undefined
): AddSightingDTO => ({
  pandaId: pandaId || "", 
  location,
  dateTime
});

export const fetchSightings = async (pandas: RedPanda[]): Promise<Sighting[]> => {
  try {
    const response = await fetchAllSightings();
    return response.data.map((sighting: SightingDTO) => ({
      ...sighting,
      location: { latitude: sighting.locationLat, longitude: sighting.locationLon },
      panda: pandas.find(panda => panda.id === sighting.pandaId)
    })).filter((sighting: Sighting) => !!sighting.panda);
  } catch {
    return [];
  }
}

export const addSighting = async (sighting: AddSightingDTO) => {
  try {
    const response = await createSighting(sighting);
    return response;
  } catch {
    return undefined;
  }
}

export const SightingService = {
  buildSighting,
  fetchSightings,
  addSighting
}
