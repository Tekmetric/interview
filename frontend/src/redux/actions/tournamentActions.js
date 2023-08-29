import * as types from "./actionTypes";
import {TournamentAPI} from "../../api/config/TournamentAPI";

export function loadTournamentSuccess(tournaments) {
    return {type: types.LOAD_TOURNAMENTS_SUCCESS, tournaments};
}

/**
 * laod all tournaments and dispatch action to update the tournaments store state
 * @param dispatch
 */
export function loadTournaments(dispatch) {
    TournamentAPI
        .getAll()
        .then(tournaments => {
            dispatch(loadTournamentSuccess(tournaments));
        })
        .catch(error => {
            throw error;
        });
}