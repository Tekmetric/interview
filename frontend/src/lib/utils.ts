// Conversion constants
const DECIMETERS_PER_METER = 10;
const DECIMETERS_TO_INCHES = 3.93701; // 1 decimeter = 3.93701 inches
const INCHES_PER_FOOT = 12;
const INCHES_ROUNDED_TO_FOOT = 12;

/**
 * Converts Pokemon height from decimeters to metric or imperial units
 *
 * @param height - Height in decimeters (API format from PokeAPI)
 * @param isMetric - Whether to use metric system (meters) or imperial (feet/inches)
 * @returns Formatted height string with units
 *
 * @example
 * ```typescript
 * convertHeight(17, false) // "5'7""
 * convertHeight(17, true)  // "1.7 m"
 * convertHeight(null, true) // "Unknown"
 * ```
 */
export const convertHeight = (height: number | null | undefined, isMetric: boolean): string => {
  if (!height) return 'Unknown';

  if (isMetric) {
    return `${(height / DECIMETERS_PER_METER).toFixed(1)} m`;
  }

  const totalInches = height * DECIMETERS_TO_INCHES;
  let feet = Math.floor(totalInches / INCHES_PER_FOOT);
  let inches = Math.round(totalInches % INCHES_PER_FOOT);

  // If inches rounds to 12, convert to 1 additional foot
  if (inches === INCHES_ROUNDED_TO_FOOT) {
    feet += 1;
    inches = 0;
  }

  return inches === 0 ? `${feet}'` : `${feet}'${inches}"`;
};

const HECTOGRAMS_PER_KILOGRAM = 10;
const KILOGRAMS_TO_POUNDS = 2.20462;

/**
 * Converts Pokemon weight from hectograms to metric or imperial units
 *
 * @param weight - Weight in hectograms (API format from PokeAPI)
 * @param isMetric - Whether to use metric system (kg) or imperial (lb)
 * @returns Formatted weight string with units
 *
 * @example
 * ```typescript
 * convertWeight(69, false) // "15.2 lb"
 * convertWeight(69, true)  // "6.9 kg"
 * convertWeight(null, true) // "Unknown"
 * ```
 */
export const convertWeight = (weight: number | null | undefined, isMetric: boolean): string => {
  if (!weight) return 'Unknown';

  if (isMetric) {
    return `${(weight / HECTOGRAMS_PER_KILOGRAM).toFixed(1)} kg`;
  }

  const kilograms = weight / HECTOGRAMS_PER_KILOGRAM;
  return `${(kilograms * KILOGRAMS_TO_POUNDS).toFixed(1)} lb`;
};

/**
 * Capitalizes the first character of a string
 *
 * @param str - String to capitalize
 * @returns String with first character uppercase
 *
 * @example
 * ```typescript
 * capitalize('pikachu') // "Pikachu"
 * capitalize('') // ""
 * capitalize(null) // ""
 * ```
 */
export const capitalize = (str: string | null | undefined): string => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1);
};
