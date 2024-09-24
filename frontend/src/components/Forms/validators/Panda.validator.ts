import { RedPanda } from "../../../types/RedPanda";
import { GlobalValidator } from "./Global.validator";
import { IPandaValidator, ValidationMessage } from "./Validator.interface";
import { isEqual } from "lodash";

const isNameValid = (name: string): ValidationMessage => GlobalValidator.isNameValid(name);

const isAgeValid = (age: number | undefined): ValidationMessage => {
  if (age === undefined) {
    return { isValid: true };
  }

  if (age < 1) {
    return { isValid: false, errorMessage: "Age must be greater than 0" };
  }
  if (age >  20) {
    return { isValid: false, errorMessage: "Age cannot be greater than 20" };
  }
  if (!Number.isInteger(age)) {
    return { isValid: false, errorMessage: "Age must be integer" };
  }

  return { isValid: true };
}

const isDirty = (initialValue: RedPanda, currentValue: RedPanda) => !isEqual(initialValue, currentValue);

const isValid = (panda: RedPanda) => isNameValid(panda.name).isValid && isAgeValid(panda.age).isValid;

export const PandaValidator: IPandaValidator = {
  isDirty,
  isNameValid,
  isAgeValid,
  isValid
}
