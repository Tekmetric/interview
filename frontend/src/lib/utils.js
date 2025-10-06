export const convertHeight = (height, isMetric) => {
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

export const convertWeight = (weight, isMetric) => {
  if (!weight) return 'Unknown';

  if (isMetric) {
    return `${(weight / 10).toFixed(1)} kg`;
  }

  return `${((weight / 10) * 2.20462).toFixed(1)} lb`;
};

export const capitalize = (str) => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1);
};
