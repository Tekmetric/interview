import type { Vehicle } from '../features/vehicles/types';

export const mockVehicles: Vehicle[] = [
  {
    id: 1,
    vin: '1HGCM87633A123456',
    make: 'Toyota',
    model: 'Corolla',
    modelYear: 2021,
    image: 'https://example.com/car.jpg',
  },
  {
    id: 2,
    vin: '2FTRX18W11232345',
    make: 'Ford',
    model: 'F-150',
    modelYear: 2022,
    image: 'https://example.com/truck.jpg',
  },
];
