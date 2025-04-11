import { FETCH_VEHICLES_REQUEST, FETCH_VEHICLES_SUCCESS, FETCH_VEHICLES_FAILURE } from './types';

// Sync Actions

// Thunks
export const fetchVehicles =
  (page = 1) =>
  async (dispatch: any) => {
    dispatch({ type: FETCH_VEHICLES_REQUEST });

    try {
      const res = await fetch(`http://localhost:8080/api/vehicles`);
      const data = await res.json();

      dispatch({ type: FETCH_VEHICLES_SUCCESS, payload: data });
    } catch (err) {
      dispatch({ type: FETCH_VEHICLES_FAILURE, error: 'ERROR OCCURRED' });
    }
  };
