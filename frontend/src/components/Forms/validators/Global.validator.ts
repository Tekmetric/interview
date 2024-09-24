import { ValidationMessage } from "./Validator.interface";

let nameRegex = /^[A-Z][a-zA-Z]+$/;

const isNameValid = (name: string): ValidationMessage => {
  if (!name) {
    return { isValid: false, errorMessage: "Name cannot be empty" };
  }
  if (name.length < 3) {
    return { isValid: false, errorMessage: "Name must be at least 3 characters long" };
  }
  if (!nameRegex.test(name)) {
    return { isValid: false, errorMessage: "Name must contain only letters, and start with a capital letter" };
  }

  return { isValid: true };
}

export const GlobalValidator = {
  isNameValid
}
