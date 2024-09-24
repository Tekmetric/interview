
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
  colour: string;
  species: RedPandaSpecies;
  name: string;
  age: number | undefined;
}
