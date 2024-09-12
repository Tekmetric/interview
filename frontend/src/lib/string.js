/**
 * Capitalizes the first character of a string.
 * @param {string} s
 * @returns {string}
 */
const capitalize = s => {
  if (typeof s !== 'string' || s.length === 0) {
    return s;
  }
  return s.substring(0, 1).toLocaleUpperCase() + s.substring(1);
};

export { capitalize };
