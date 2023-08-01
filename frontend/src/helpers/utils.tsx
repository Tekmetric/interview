const EUR = 'EUR';
const LOCALE = 'en-US';
const formatNumberValue = (item: number) =>
  item.toLocaleString(LOCALE, {
    style: 'currency',
    currencyDisplay: 'code',
    currency: EUR,
  });

const getNotificationMessage = (
  firstIndex: number,
  lastIndex: number,
): string => {
  if (firstIndex < lastIndex) {
    return 'Product added to cart';
  } else if (firstIndex > lastIndex && lastIndex !== 0) {
    return 'Product removed from cart';
  }

  return '';
};

export { formatNumberValue, getNotificationMessage };
