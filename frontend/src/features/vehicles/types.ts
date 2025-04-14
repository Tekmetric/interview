// Action Types
export const FETCH_VEHICLES_REQUEST = 'FETCH_VEHICLES_REQUEST';
export const FETCH_VEHICLES_SUCCESS = 'FETCH_VEHICLES_SUCCESS';
export const FETCH_VEHICLES_FAILURE = 'FETCH_VEHICLES_FAILURE';

export const FETCH_VEHICLE_DETAIL_REQUEST = 'FETCH_VEHICLE_DETAIL_REQUEST';
export const FETCH_VEHICLE_DETAIL_SUCCESS = 'FETCH_VEHICLE_DETAIL_SUCCESS';
export const FETCH_VEHICLE_DETAIL_FAILURE = 'FETCH_VEHICLE_DETAIL_FAILURE';

export const CREATE_VEHICLE_REQUEST = 'CREATE_VEHICLE_REQUEST';
export const CREATE_VEHICLE_FAILURE = 'CREATE_VEHICLE_FAILURE';

export const DELETE_VEHICLE_REQUEST = 'DELETE_VEHICLE_REQUEST';
export const DELETE_VEHICLE_SUCCESS = 'DELETE_VEHICLE_SUCCESS';
export const DELETE_VEHICLE_FAILURE = 'DELETE_VEHICLE_FAILURE';

export const UPDATE_VEHICLE_REQUEST = 'UPDATE_VEHICLE_REQUEST';
export const UPDATE_VEHICLE_SUCCESS = 'UPDATE_VEHICLE_SUCCESS';
export const UPDATE_VEHICLE_FAILURE = 'UPDATE_VEHICLE_FAILURE';

export const SET_SELECTED_VEHICLE = 'SET_SELECTED_VEHICLE';

export interface Vehicle {
  id: number | string;
  vin: string;
  make: string;
  model: string;
  modelYear: number;
  image: string;
}

export interface MetaData {
  currentPage: number;
  itemsPerPage: number;
  totalItems: number;
  totalPages: number;
}

export interface VehiclesState {
  vehicles: {
    data: Vehicle[];
    meta: MetaData;
  };
  selectedVehicle?: Vehicle;
  loading: boolean;
  error: string | null;
}
