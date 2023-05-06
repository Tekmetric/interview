import dayjs from 'dayjs';

export const ROOT_URL = 'http://localhost:3001';

export const CARS = [
  {
    id: 1,
    brand: 'BMW',
    color: 'Black',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus venenatis ac nulla nec aliquam. Maecenas tincidunt metus non porta fringilla. Ut suscipit eu nibh et vehicula. Nulla ut velit sapien. Vestibulum sagittis semper nulla at congue. Nullam gravida, ipsum quis fermentum pretium, sem nisl interdum eros, non maximus lacus tortor.',
    model: '3 Series',
    engineCapacity: 1996,
    year: 2006,
    minPrice: 4000,
    maxPrice: 8000,
    url: 'https://www.topgear.com/sites/default/files/images/news-article/2015/07/75188e63e4d7cff5b227b81467219769/e90_1.jpg'
  },
  {
    id: 2,
    brand: 'Audi',
    color: 'White',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus venenatis ac nulla nec aliquam. Maecenas tincidunt metus non porta fringilla. Ut suscipit eu nibh et vehicula. Nulla ut velit sapien. Vestibulum sagittis semper nulla at congue. Nullam gravida, ipsum quis fermentum pretium, sem nisl interdum eros, non maximus lacus tortor.',
    model: 'A4',
    engineCapacity: 1993,
    year: 2015,
    minPrice: 15000,
    maxPrice: 25000,
    url: 'https://hips.hearstapps.com/hmg-prod/amv-prod-cad-assets/images/14q1/562749/2015-audi-a4-feature-car-and-driver-photo-581834-s-original.jpg?fill=2:1&resize=1200:*'
  },
  {
    id: 3,
    brand: 'Mercedes',
    color: 'Gray',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus venenatis ac nulla nec aliquam. Maecenas tincidunt metus non porta fringilla. Ut suscipit eu nibh et vehicula. Nulla ut velit sapien. Vestibulum sagittis semper nulla at congue. Nullam gravida, ipsum quis fermentum pretium, sem nisl interdum eros, non maximus lacus tortor.',
    model: 'C Class',
    engineCapacity: 1989,
    year: 2015,
    minPrice: 18000,
    maxPrice: 32000,
    url: 'https://www.carscoops.com/wp-content/uploads/2013/12/2015-Mercedes-C-Class-01.jpg'
  },
  {
    id: 4,
    brand: 'Kia',
    color: 'Penta Metal',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus venenatis ac nulla nec aliquam. Maecenas tincidunt metus non porta fringilla. Ut suscipit eu nibh et vehicula. Nulla ut velit sapien. Vestibulum sagittis semper nulla at congue. Nullam gravida, ipsum quis fermentum pretium, sem nisl interdum eros, non maximus lacus tortor.',
    model: 'Sportage',
    engineCapacity: 1588,
    year: 2022,
    minPrice: 25000,
    maxPrice: 49000,
    url: 'https://www.kia.ro/static/newsite/content/images/models//sportage-hev-2022/highlight/kia-sportage-2022-functionalitate-5.jpg'
  }
];

export const INITIAL_CAR_VALUES = {
  brand: '',
  color: '',
  description: '',
  model: '',
  engineCapacity: 1000,
  year: dayjs().year(),
  minPrice: 1000,
  maxPrice: 500000,
  url: ''
};
