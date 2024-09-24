import React from "react";
import { TextField } from "@mui/material";
import { IAgeInputProps } from "./PandaForm.interface";
import { PandaValidator } from "../validators/Panda.validator";

export default function AgeInput({ value, onChange }: IAgeInputProps) {
  const handleAgeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    try {
      onChange(parseInt(event.target.value, 10));
    } catch {
      onChange(0);
    }
  }

  return (
    <TextField
      required
      id="outlined-required"
      label="Age"
      type="number"
      value={value || ""}
      onChange={handleAgeChange}
      error={value ? !PandaValidator.isAgeValid(value).isValid : false}
      helperText={value && PandaValidator.isAgeValid(value).errorMessage}
      fullWidth
    />
  );
};
