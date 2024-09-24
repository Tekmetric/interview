import React from "react";
import { TextField } from "@mui/material";
import { INameInputProps } from "./PandaForm.interface";
import { PandaValidator } from "../validators/Panda.validator";

export default function NameInput({ value, onChange }: INameInputProps) {
  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChange(event.target.value);
  }

  return (
    <TextField
      required
      id="outlined-required"
      label="Name"
      value={value}
      onChange={handleNameChange}
      error={value ? !PandaValidator.isNameValid(value).isValid : false}
      helperText={value && PandaValidator.isNameValid(value).errorMessage}
      fullWidth
    />
  );
};
