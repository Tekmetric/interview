import React from "react";
import { FormControl, FormControlLabel, FormLabel, Radio, RadioGroup } from "@mui/material";
import { RedPandaSpecies, RedPandaSpeciesLabels } from "../../../types/RedPanda";
import { ISpeciesInputProps } from "./PandaForm.interface";

export default function SpeciesInput({ value, onChange }: ISpeciesInputProps) {

  const handleSpeciesChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = (event.target as HTMLInputElement).value === RedPandaSpecies.Himalayan.toString()
      ? RedPandaSpecies.Himalayan
      : RedPandaSpecies.Chinese;
    onChange(value);
  };
  
  return (
    <FormControl>
      <FormLabel id="panda-species">Species</FormLabel>
      <RadioGroup
        row
        aria-labelledby="panda-species"
        name="panda-species-radio-buttons-group"
        value={value}
        onChange={handleSpeciesChange}
      >
        <FormControlLabel
          value={RedPandaSpecies.Himalayan.toString()}
          control={<Radio />} label={RedPandaSpeciesLabels[RedPandaSpecies.Himalayan]}
        /> 
        <FormControlLabel
          value={RedPandaSpecies.Chinese.toString()}
          control={<Radio />}
          label={RedPandaSpeciesLabels[RedPandaSpecies.Chinese]}
        />
      </RadioGroup>
    </FormControl> 
  );
}
