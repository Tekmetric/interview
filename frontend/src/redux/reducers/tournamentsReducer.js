import * as types from "../actions/actionTypes";
import initialState from "./initialState";

export default function tournamentsReducer(state = initialState.tournaments, action) {
    switch (action.type) {
        case types.LOAD_TOURNAMENTS_SUCCESS:
            return action.tournaments;
        default:
            return state;
    }
}
