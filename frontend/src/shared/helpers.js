export function truncate(s, value) {
  return s.substring(0, value) + '...';
}

const placeholderImage = require('../assets/images/placeholder-image.png');
export const getImage = (carImageUrl) => {
  return carImageUrl || placeholderImage;
};
