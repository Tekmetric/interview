import * as types from "./actionTypes";
import {PlayerAPI} from "../../api/config/PlayerAPI";

export function loadPlayersSuccess(players) {
  return { type: types.LOAD_PLAYERS_SUCCESS, players };
}

export function createPlayersSuccess(player) {
  return { type: types.CREATE_PLAYER_SUCCESS, player };
}

export function updatePlayersSuccess(player) {
  return { type: types.UPDATE_PLAYER_SUCCESS, player };
}

export function deletePlayerSuccess(player) {
  return { type: types.DELETE_PLAYER_SUCCESS, player };
}

/**
 * load all players and dispatch action to update the players store state
 * @param dispatch
 */
export function loadPlayers(dispatch) {
  PlayerAPI
      .getAll()
      .then(players => {
        dispatch(loadPlayersSuccess(players));
      })
      .catch(error => {
        throw error;
      });
}

/**
 * Delete player from list and dispatch action to update the players store state
 * @param dispatch
 * @param player
 */
export function deletePlayerData(dispatch, player) {
    PlayerAPI
        .delete(player.id)
        .then(() => {
            dispatch(deletePlayerSuccess(player));
        })
        .catch(error => {
            throw error;
        });
}
