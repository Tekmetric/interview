import * as types from "./actionTypes";
import {RacquetsAPI} from "../../api/config/RacquetsAPI";

export function loadRacquetsSuccess(racquets) {
  return { type: types.LOAD_RACQUETS_SUCCESS, racquets };
}

/**
 * laod all racquets and dispatch action to update the racquets store state
 * @param dispatch
 */
export function loadRacquets(dispatch) {
  RacquetsAPI
      .getAll()
      .then(racquets => {
        dispatch(loadRacquetsSuccess(racquets));
      })
      .catch(error => {
        throw error;
      });
}