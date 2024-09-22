
export enum RedPandaSpecies {
  Himalayan,
  Chinese
}

export type RedPanda = {
  id: string;
  hasTracker: boolean;
  colour: string;
  species: RedPandaSpecies;
  name: string | undefined;
  age: number | undefined;
}
