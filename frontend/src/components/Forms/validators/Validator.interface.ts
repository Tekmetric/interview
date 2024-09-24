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
