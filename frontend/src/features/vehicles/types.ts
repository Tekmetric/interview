// Action Type Constants
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
export const UPDATE_VEHICLE_FAILURE = 'UPDATE_VEHICLE_FAILURE';

export const SET_SELECTED_VEHICLE = 'SET_SELECTED_VEHICLE';

// Action Interfaces

// Fetch Vehicles
export interface FetchVehiclesRequestAction {
  type: typeof FETCH_VEHICLES_REQUEST;
}

export interface FetchVehiclesSuccessAction {
  type: typeof FETCH_VEHICLES_SUCCESS;
  payload: {
    data: Vehicle[];
    meta: MetaData;
  };
}

export interface FetchVehiclesFailureAction {
  type: typeof FETCH_VEHICLES_FAILURE;
  payload: string;
}

// Fetch Vehicle Detail
export interface FetchVehicleDetailRequestAction {
  type: typeof FETCH_VEHICLE_DETAIL_REQUEST;
}

export interface FetchVehicleDetailSuccessAction {
  type: typeof FETCH_VEHICLE_DETAIL_SUCCESS;
  payload: Vehicle;
}

export interface FetchVehicleDetailFailureAction {
  type: typeof FETCH_VEHICLE_DETAIL_FAILURE;
  error: string;
}

// Create Vehicle
export interface CreateVehicleRequestAction {
  type: typeof CREATE_VEHICLE_REQUEST;
}

export interface CreateVehicleFailureAction {
  type: typeof CREATE_VEHICLE_FAILURE;
  payload: string;
}

// Delete Vehicle
export interface DeleteVehiclesRequestAction {
  type: typeof DELETE_VEHICLE_REQUEST;
  payload: { id: number };
}

export interface DeleteVehiclesSuccessAction {
  type: typeof DELETE_VEHICLE_SUCCESS;
}

export interface DeleteVehiclesFailureAction {
  type: typeof DELETE_VEHICLE_FAILURE;
  error: string;
  payload: {
    index: number;
    vehicle: Vehicle;
  };
}

// Update Vehicle
export interface UpdateVehicleRequestAction {
  type: typeof UPDATE_VEHICLE_REQUEST;
}

export interface UpdateVehicleFailureAction {
  type: typeof UPDATE_VEHICLE_FAILURE;
  payload: string;
}

export interface setSelectedVehicleAction {
  type: typeof SET_SELECTED_VEHICLE;
  payload: Vehicle;
}

// Combine all action interfaces
export type VehicleAction =
  | FetchVehiclesRequestAction
  | FetchVehiclesSuccessAction
  | FetchVehiclesFailureAction
  | FetchVehicleDetailRequestAction
  | FetchVehicleDetailSuccessAction
  | FetchVehicleDetailFailureAction
  | CreateVehicleRequestAction
  | CreateVehicleFailureAction
  | DeleteVehiclesRequestAction
  | DeleteVehiclesSuccessAction
  | DeleteVehiclesFailureAction
  | UpdateVehicleRequestAction
  | UpdateVehicleFailureAction
  | setSelectedVehicleAction;

// Data Types
export interface Vehicle {
  id: number;
  vin: string;
  make: string;
  model: string;
  modelYear: number;
  image: any;
}

export interface MetaData {
  currentPage: number;
  itemsPerPage: number;
  totalItems: number;
  totalPages: number;
}

// Props / State Types
export interface VehiclesState {
  vehicles: {
    data: Vehicle[];
    meta: MetaData;
  };
  selectedVehicle?: Vehicle;
  loading: boolean;
  error: string | null;
}

export interface VehicleFormHeaderProps {
  isEditMode: boolean;
  isDisabled: boolean;
  onEditClick: () => void;
  vehicleTitle: string;
  imageUrl: string | null;
  onDeleteImage: () => void;
}

export interface VehicleFormFieldsProps {
  isDisabled: boolean;
  fileRef: React.RefObject<HTMLInputElement | null>;
  setPreviewImage: (img: string | null) => void;
  setHasClearedImage: (cleared: boolean) => void;
}
