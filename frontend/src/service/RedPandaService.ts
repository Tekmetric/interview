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

const initDefaultPandaObject = (): RedPanda => ({
  id: "",
  name: "",
  age: undefined,
  colour: redPandaColours[0],
  hasTracker: false,
  species: RedPandaSpecies.Himalayan,
});

const initFromPanda = (panda?: RedPanda): RedPanda => ({
  id: panda?.id || "",
  name: panda?.name || "",
  age: panda?.age,
  colour: panda?.colour || redPandaColours[0],
  hasTracker: panda?.hasTracker || false,
  species: panda?.species || RedPandaSpecies.Himalayan,
});

const buildPanda = (
  id: string | undefined,
  name: string | undefined,
  age: number | undefined,
  species: RedPandaSpecies,
  hasTracker: boolean,
  colour: string
): RedPanda => ({
  id: id || "",
  name: name || "", 
  age,
  colour,
  hasTracker, 
  species
})

export const RedPandaService = {
  initDefaultPandaObject,
  initFromPanda,
  buildPanda
}
