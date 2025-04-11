// Action Types
export const FETCH_VEHICLES_REQUEST = 'FETCH_VEHICLES_REQUEST';
export const FETCH_VEHICLES_SUCCESS = 'FETCH_VEHICLES_SUCCESS';
export const FETCH_VEHICLES_FAILURE = 'FETCH_VEHICLES_FAILURE';

export interface Vehicle {
  id: number;
  vin: string;
  make: string;
  model: string;
  modelYear: number;
  image: string;
}

