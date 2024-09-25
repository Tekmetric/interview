import { Location } from "../../../types/Location";

export interface IFormValidator {
  isDirty: (initialValue: any, currentValue: any) => boolean;
  isValid: (value: any) => boolean;
}

export type ValidationMessage = {
  isValid: boolean;
  errorMessage?: string;
}

export interface IPandaValidator extends IFormValidator {
  isNameValid: (name: string) => ValidationMessage;
  isAgeValid: (age: number) => ValidationMessage;
}

export interface ISightingValidator extends IFormValidator {
  isDateTimeValid: (dateTime: string) => ValidationMessage;
  isLocationValid: (location: Location) => ValidationMessage;
  isRedPandaValid: (pandaId: string | undefined) => ValidationMessage;
}
