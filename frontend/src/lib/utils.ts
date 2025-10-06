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
    return `${(height / 10).toFixed(1)} m`;
  }

  const totalInches = (height / 10) * 39.3701;
  let feet = Math.floor(totalInches / 12);
  let inches = Math.round(totalInches % 12);

  // If inches rounds to 12, convert to 1 additional foot
  if (inches === 12) {
    feet += 1;
    inches = 0;
  }

  return inches === 0 ? `${feet}'` : `${feet}'${inches}"`;
};

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
    return `${(weight / 10).toFixed(1)} kg`;
  }

  return `${((weight / 10) * 2.20462).toFixed(1)} lb`;
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
