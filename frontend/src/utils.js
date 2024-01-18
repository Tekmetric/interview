export const toPercent = (number) => {
    return new Intl.NumberFormat('en-US', { style: 'percent', maximumSignificantDigits: 3 }).format(number);
}

export const toUSD = (number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(number);
}