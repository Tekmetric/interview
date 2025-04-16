import {
  CREATE_VEHICLE_FAILURE,
  CREATE_VEHICLE_REQUEST,
  DELETE_VEHICLE_FAILURE,
  DELETE_VEHICLE_REQUEST,
  FETCH_VEHICLE_DETAIL_FAILURE,
  FETCH_VEHICLE_DETAIL_REQUEST,
  FETCH_VEHICLE_DETAIL_SUCCESS,
  FETCH_VEHICLES_FAILURE,
  FETCH_VEHICLES_REQUEST,
  FETCH_VEHICLES_SUCCESS,
  SET_SELECTED_VEHICLE,
  UPDATE_VEHICLE_FAILURE,
  UPDATE_VEHICLE_REQUEST,
  Vehicle,
  VehicleAction,
  VehiclesState,
} from './types';

const initialState: VehiclesState = {
  vehicles: {
    data: [],
    meta: {
      currentPage: 1,
      itemsPerPage: 0,
      totalItems: 0,
      totalPages: 0,
    },
  },
  selectedVehicle: undefined,
  loading: false,
  error: null,
};

const vehicleReducer = (state = initialState, action: VehicleAction) => {
  switch (action.type) {
    case FETCH_VEHICLES_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case FETCH_VEHICLES_SUCCESS:
      return {
        ...state,
        loading: false,
        error: null,
        vehicles: {
          data: [...action.payload.data],
          meta: { ...action.payload.meta },
        },
      };
    case FETCH_VEHICLES_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    case FETCH_VEHICLE_DETAIL_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case FETCH_VEHICLE_DETAIL_SUCCESS:
      return {
        ...state,
        loading: false,
        error: null,
        selectedVehicle: { ...action.payload },
      };
    case FETCH_VEHICLE_DETAIL_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.error,
      };
    case CREATE_VEHICLE_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    // case CREATE_VEHICLE_SUCCESS:
    case CREATE_VEHICLE_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    case UPDATE_VEHICLE_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    // case CREATE_VEHICLE_SUCCESS:
    case UPDATE_VEHICLE_FAILURE:
      return {
        ...state,
        loading: false,
        error: action.payload,
      };
    case DELETE_VEHICLE_REQUEST:
      // optimistic deleting of Vehicle
      const filteredVehicles = state.vehicles.data.filter(
        (vehicle: Vehicle) => vehicle.id !== action.payload.id
      );

      return {
        ...state,
        vehicles: {
          ...state.vehicles,
          data: filteredVehicles,
        },
        loading: true,
        error: null,
      };
    // case DELETE_VEHICLE_SUCCESS:
    case DELETE_VEHICLE_FAILURE:
      // determine original location of index and re-add for optimistic deletes
      const vehicles = [...state.vehicles.data];
      vehicles.splice(action.payload.index, 0, action.payload.vehicle);

      return {
        ...state,
        loading: false,
        error: action.error,
        vehicles: {
          data: vehicles,
          meta: { ...state.vehicles.meta },
        },
      };
    case SET_SELECTED_VEHICLE:
      return {
        ...state,
        selectedVehicle: { ...action.payload },
      };
    default:
      return state;
  }
};

export default vehicleReducer;
