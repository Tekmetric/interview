
export enum RedPandaSpecies {
  Himalayan,
  Chinese,
}

export const RedPandaSpeciesLabels = {
  [RedPandaSpecies.Himalayan]: "Himalayan",
  [RedPandaSpecies.Chinese]: "Chinese",
}

export type RedPanda = {
  id: string;
  hasTracker: boolean;
  color: string;
  species: RedPandaSpecies;
  name: string;
  age: number | undefined;
}

export type RedPandaDetailDTO = RedPanda & {
  mostRecentSighting: MostRecentSighting;
}

type MostRecentSighting = {
  dateTime: string | null;
  locationLat: number | null;
  locationLon: number | null;
}
