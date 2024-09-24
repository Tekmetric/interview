import { redPandaColours } from "../constants/panda.constants";
import { Location } from "../types/Location";
import { RedPandaSpecies } from "../types/RedPanda";
import { Sighting, SightingDTO } from "../types/Sighting";

export const sightingsMock: Sighting[] = [
  {
    id: 'test0',
    dateTime: new Date().toISOString(),
    location: { lat: 28.3974, lon: 83.1258 },
    panda: {
      id: 'test0',
      age: 10,
      color: redPandaColours[0],
      hasTracker: true,
      name: "Kylo",
      species: RedPandaSpecies.Himalayan
    },
  },
  {
    id: 'test1',
    dateTime: new Date().toISOString(),
    location: {lat: 28.0, lon: 84.1258},
    panda: {
      id: 'test1',
      age: 3,
      color: redPandaColours[1],
      hasTracker: false,
      name: "Snitzel",
      species: RedPandaSpecies.Chinese
      }
  },
  {
    id: 'test2',
    dateTime: new Date().toISOString(),
    location: {lat: 27.3974, lon: 82.1258},
    panda: {
      id: 'test2',
      age: 3,
      color: redPandaColours[2],
      hasTracker: true,
      name: "Tofu",
      species: RedPandaSpecies.Chinese
    }
  },
  {
    id: 'test3',
    dateTime: new Date().toISOString(),
    location: {lat: 29.3974, lon: 85.1258},
    panda: {
      id: 'test3',
      age: 4,
      color: redPandaColours[3],
      hasTracker: false,
      name: "Pixel",
      species: RedPandaSpecies.Chinese
    }
  }
];

const buildSighting = (
  pandaId: string | undefined,
  location: Location | undefined,
  dateTime: string | undefined
): SightingDTO => ({
  pandaId: pandaId || "", 
  location, 
  dateTime
});

export const SightingService = {
  buildSighting
}
