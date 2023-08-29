import * as types from "../actions/actionTypes";
import initialState from "./initialState";

export default function tournamentsReducer(state = initialState.racquets, action) {
    switch (action.type) {
        case types.LOAD_RACQUETS_SUCCESS:
            return action.racquets;
        default:
            return state;
    }
}
