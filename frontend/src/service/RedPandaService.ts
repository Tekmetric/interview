import { createPanda, deletePanda, fetchAllPandas, getPandaById, updatePanda } from "../api/panda.api";
import { redPandaColours } from "../constants/panda.constants";
import { RedPanda, RedPandaSpecies } from "../types/RedPanda";

const initDefaultPandaObject = (): RedPanda => ({
  id: "",
  name: "",
  age: undefined,
  color: redPandaColours[0],
  hasTracker: false,
  species: RedPandaSpecies.Himalayan,
});

const initFromPanda = (panda?: RedPanda): RedPanda => ({
  id: panda?.id || "",
  name: panda?.name || "",
  age: panda?.age,
  color: panda?.color || redPandaColours[0],
  hasTracker: panda?.hasTracker || false,
  species: panda?.species || RedPandaSpecies.Himalayan,
});

const buildPanda = (
  id: string | undefined,
  name: string | undefined,
  age: number | undefined,
  species: RedPandaSpecies,
  hasTracker: boolean,
  color: string
): RedPanda => ({
  id: id || "",
  name: name || "", 
  age,
  color,
  hasTracker, 
  species
});

export const fetchPandas = async (): Promise<RedPanda[]> => {
  try {
    const response = await fetchAllPandas();
    return response.data;
  } catch {
    return [];
  }
}

export const getById = async (id: string): Promise<RedPanda | undefined> => {
  try {
    const response = await getPandaById(id);
    debugger;
    return response.data;
  } catch {
    return undefined;
  }
};

export const addPanda = async (panda: RedPanda) => {
  try {
    const response = await createPanda(panda);
    debugger;
    return response;
  } catch {
    return undefined;
  }
}

export const editPanda = async (panda: RedPanda) => {
  try {
    const response = await updatePanda(panda);
    debugger;
    return response;
  } catch {
    return undefined;
  }
}

export const deleteById = async (pandaId: string) => {
  try {
    const response = await deletePanda(pandaId);
    debugger;
    return response;
  } catch {
    return undefined;
  }
}

export const RedPandaService = {
  initDefaultPandaObject,
  initFromPanda,
  buildPanda,
  fetchPandas,
  addPanda,
  getById,
  editPanda, 
  deleteById
}
