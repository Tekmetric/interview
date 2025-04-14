import {
  CREATE_VEHICLE_FAILURE,
  CREATE_VEHICLE_REQUEST,
  DELETE_VEHICLE_REQUEST,
  DELETE_VEHICLE_FAILURE,
  DELETE_VEHICLE_SUCCESS,
  FETCH_VEHICLES_FAILURE,
  FETCH_VEHICLES_REQUEST,
  FETCH_VEHICLES_SUCCESS,
  FETCH_VEHICLE_DETAIL_REQUEST,
  FETCH_VEHICLE_DETAIL_SUCCESS,
  FETCH_VEHICLE_DETAIL_FAILURE,
  UPDATE_VEHICLE_REQUEST,
  UPDATE_VEHICLE_FAILURE,
  SET_SELECTED_VEHICLE,
  Vehicle,
} from './types';

import { setNotice } from '../application/actions';
import { AppDispatch } from '../../store/store';

// Sync Actions
export const setSelectedVehicle = (vehicle: Vehicle | null) => {
  console.log('setting vehicle', vehicle);
  return {
    type: SET_SELECTED_VEHICLE,
    payload: vehicle,
  };
};

// Thunks
export const fetchVehicles =
  (page = 1, size = 20) =>
  async (dispatch: AppDispatch) => {
    dispatch({ type: FETCH_VEHICLES_REQUEST });

    try {
      const res = await fetch(`http://localhost:8080/api/vehicles?page=${page}&size=${size}`);
      const data = await res.json();

      dispatch({ type: FETCH_VEHICLES_SUCCESS, payload: data });
    } catch (err) {
      dispatch({ type: FETCH_VEHICLES_FAILURE, error: 'ERROR OCCURRED' });
    }
  };

export const fetchVehicleById = (id: number) => async (dispatch: AppDispatch) => {
  dispatch({ type: FETCH_VEHICLE_DETAIL_REQUEST });
  try {
    const res = await fetch(`http://localhost:8080/api/vehicles/${id}`);
    const data = await res.json();

    dispatch({ type: FETCH_VEHICLE_DETAIL_SUCCESS, payload: data });
  } catch (err) {
    dispatch({ type: FETCH_VEHICLE_DETAIL_FAILURE, error: 'ERROR OCCURRED' });
  }
};

export const deleteVehicleById = (id: number) => async (dispatch: AppDispatch, getState: any) => {
  // get vehicle and index (for request failure optimistic deletes)
  const vehicles = getState().vehicles.vehicles.data;
  const index = vehicles.findIndex((vehicle: Vehicle) => vehicle.id === id);
  const vehicle = index !== -1 ? vehicles[index] : null;

  // optimistic deleting of Vehicle
  dispatch({ type: DELETE_VEHICLE_REQUEST, payload: vehicle });

  try {
    const res = await fetch(`http://localhost:8080/api/vehicles/${id}`, {
      method: 'DELETE',
    });

    if (res.ok) {
      dispatch({ type: DELETE_VEHICLE_SUCCESS, payload: id });

      const currentPage = getState().vehicles.vehicles.meta.currentPage;

      dispatch(fetchVehicles(currentPage));
      dispatch(
        setNotice({
          type: 'success',
          message: 'vehicle successfully deleted',
        })
      );
    } else {
      throw new Error('Failed to delete vehicle');
    }
  } catch (error) {
    const payload = { vehicle, index };
    const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';

    dispatch({ type: DELETE_VEHICLE_FAILURE, payload, error: errorMessage });
    dispatch(
      setNotice({
        type: 'error',
        message: 'error deleting vehicle',
      })
    );
  }
};

export const updateVehicleById =
  (id: number, vehicle: Partial<Vehicle>) => async (dispatch: AppDispatch, getState: any) => {
    dispatch({ type: UPDATE_VEHICLE_REQUEST });

    try {
      const res = await fetch(`http://localhost:8080/api/vehicles/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(vehicle),
      });

      if (res.ok) {
        // dispatch({ type: UPDATE_VEHICLE_SUCCESS, payload: id });
        // Get the current page from the state
        const currentPage = getState().vehicles.vehicles.meta.currentPage;

        dispatch(fetchVehicles(currentPage));
        dispatch(
          setNotice({
            type: 'success',
            message: 'vehicle updated',
          })
        );
      } else {
        throw new Error('Failed to delete vehicle');
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';

      dispatch({ type: UPDATE_VEHICLE_FAILURE, error: errorMessage });
      dispatch(
        setNotice({
          type: 'error',
          message: 'error updating vehicle',
        })
      );
    }
  };

export const createVehicle =
  (vehicle: Partial<Vehicle>) => async (dispatch: AppDispatch, getState: any) => {
    dispatch({ type: CREATE_VEHICLE_REQUEST });

    try {
      const res = await fetch(`http://localhost:8080/api/vehicles`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(vehicle),
      });

      if (res.ok) {
        const currentPage = getState().vehicles.vehicles.meta.currentPage;

        dispatch(fetchVehicles(currentPage));
        dispatch(
          setNotice({
            type: 'success',
            message: 'new vehicle successfully added',
          })
        );
      } else {
        throw new Error('Failed to delete vehicle');
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';

      dispatch({ type: CREATE_VEHICLE_FAILURE, error: errorMessage });
      dispatch(
        setNotice({
          type: 'error',
          message: 'error adding vehicle',
        })
      );
    }
  };
