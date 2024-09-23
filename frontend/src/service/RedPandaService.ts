import { redPandaColours } from "../constants/panda.constants";
import { RedPanda, RedPandaSpecies } from "../types/RedPanda";

export const pandaMock: RedPanda[] = [
  {
    id: 'test0',
    age: 10,
    colour: redPandaColours[0],
    hasTracker: true,
    name: "Kylo",
    species: RedPandaSpecies.Himalayan
  },
  {
    id: 'test1',
    age: 3,
    colour: redPandaColours[1],
    hasTracker: false,
    name: "Snitzel",
    species: RedPandaSpecies.Chinese
  },
  {
    id: 'test2',
    age: 3,
    colour: redPandaColours[2],
    hasTracker: true,
    name: "Tofu",
    species: RedPandaSpecies.Chinese
  },
  {
    id: 'test3',
    age: 4,
    colour: redPandaColours[3],
    hasTracker: false,
    name: "Pixel",
    species: RedPandaSpecies.Chinese
  }
];
