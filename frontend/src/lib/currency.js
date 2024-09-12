const PRICE_UNKNOWN_STRING = 'Price Unknown';

const USDFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
});

/**
 * Given a USD float value, sticks a dollar sign in front of it;
 * in the event that no value is provided, returns a special string
 * signifying the price is unknown.
 * @param {number|null} amount
 * @returns {string}
 */
const dollarize = n => {
  if (n !== null) {
    return USDFormatter.format(n);
  } else {
    return PRICE_UNKNOWN_STRING;
  }
};

export { dollarize };
