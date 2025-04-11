import { FETCH_VEHICLES_REQUEST, FETCH_VEHICLES_SUCCESS, FETCH_VEHICLES_FAILURE } from './types';
import { Vehicle } from './types';

interface VehiclesState {
  vehicles: Vehicle[];
  loading: boolean;
  error: string | null;
  page: number;
  totalPages: number;
}

const initialState: VehiclesState = {
  vehicles: [],
  loading: false,
  error: null,
  page: 1,
  totalPages: 1,
};

const vehicleReducer = (state = initialState, action: any) => {
  switch (action.type) {
    case FETCH_VEHICLES_REQUEST:
      return { ...state, loading: true };
    case FETCH_VEHICLES_SUCCESS:
      console.log('fetching successful');
      return { ...state, loading: false, vehicles: [...action.payload] };
    case FETCH_VEHICLES_FAILURE:
      console.log('fetching failed');
      return { ...state, loading: false, error: 'an unknown error occurred' };
    default:
      return state;
  }
};

export default vehicleReducer;
