import { Location } from "../../../types/Location";
import { AddSightingDTO, SightingDTO } from "../../../types/Sighting";
import { GlobalValidator } from "./Global.validator";
import { ISightingValidator, ValidationMessage } from "./Validator.interface";
import { isEqual } from "lodash";

const isDateTimeValid = (dateTime: string | undefined): ValidationMessage => GlobalValidator.isDateTimeValid(dateTime);

const isLocationValid = (location: Location | undefined): ValidationMessage => {
  if (!location) {
    return { isValid: false, errorMessage: "Location is required" };
  }

  if (location.latitude > 90 || location.latitude < -90) {
    return { isValid: false, errorMessage: "Latitude must be between -90 and 90 degrees" };
  }

  if (location.longitude > 180 || location.longitude < -180) {
    return { isValid: false, errorMessage: "Longitude must be between -180 and 180 degrees" };
  }

  return { isValid: true };
}

const isRedPandaValid = (pandaId: string | undefined): ValidationMessage => {
  if (!pandaId) {
    return { isValid: false, errorMessage: "Red panda is required" };
  }

  return { isValid: true };
}

const isDirty = (initialValue: SightingDTO, currentValue: SightingDTO) => !isEqual(initialValue, currentValue);

const isValid = (sighting: AddSightingDTO) => (
  isDateTimeValid(sighting.dateTime).isValid
  && isLocationValid(sighting.location).isValid
  && isRedPandaValid(sighting.pandaId).isValid
);

export const SightingValidator: ISightingValidator = {
  isDirty,
  isDateTimeValid,
  isLocationValid,
  isRedPandaValid,
  isValid
}
