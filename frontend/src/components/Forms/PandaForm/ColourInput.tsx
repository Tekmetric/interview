import React from "react";
import { FormControl, FormLabel, Radio, RadioGroup } from "@mui/material";
import { IColourInputProps } from "./PandaForm.interface";
import { redPandaColours } from "../../../constants/panda.constants";
import { ColourPickerIcon, ColourPickerIconChecked } from "./PandaForm.style";

export default function ColourInput({ value, onChange }: IColourInputProps) {
  const handleColourChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChange(event.target.value);
  };

  return (  
    <FormControl>
      <FormLabel id="panda-Colour">Colour</FormLabel>
      <RadioGroup
        row
        aria-labelledby="panda-Colour"
        name="panda-Colour-radio-buttons-group"
        value={value}
        onChange={handleColourChange}
      >
        {
          redPandaColours.map(color => (
            <Radio
              key={color}
              value={color}
              checkedIcon={<ColourPickerIconChecked />}
              icon={<ColourPickerIcon />}
              sx={{ backgroundColor: color, margin: 1 }}
            />
          ))
        }
      </RadioGroup>
    </FormControl>           
  );
}
