export const updateCache = (date, imageDetails) => {
  localStorage.setItem(date, JSON.stringify(imageDetails));
};

export const checkCache = (date) => {
  const cached = localStorage.getItem(date);
  if (cached) {
    return JSON.parse(cached);
  }
};
