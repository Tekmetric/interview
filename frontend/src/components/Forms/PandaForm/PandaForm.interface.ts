import { RedPanda, RedPandaSpecies } from "../../../types/RedPanda";

export interface IPandaFormProps {
  panda?: RedPanda;
  onSave: (panda: RedPanda) => void;
  onDiscard: () => void;
}

export interface INameInputProps {
  value: string | undefined;
  onChange: (newValue: string | undefined) => void;
}

export interface IAgeInputProps {
  value: number | undefined;
  onChange: (newValue: number) => void;
}

export interface ISpeciesInputProps {
  value: RedPandaSpecies;
  onChange: (newValue: RedPandaSpecies) => void;
}

export interface IColourInputProps {
  value: string | undefined;
  onChange: (newValue: string) => void;
}

export interface IPandaAvatarProps {
  hasTracker: boolean;
  species: RedPandaSpecies;
}
