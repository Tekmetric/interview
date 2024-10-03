/**
 * Capitalize the first letter of a string
 *
 * @param {string} text - The text to capitalize
 * @return {string} The capitalized text
 */
export function capitalize(text: string): string {
  if (!text) return '';
  return text.charAt(0).toUpperCase() + text.slice(1);
}
