import {combineReducers} from "redux";
import players from "./playerReducer";
import tournaments from "./tournamentsReducer";
import racquets from "./racquetsReducer";

const rootReducer = combineReducers({
    players,
    tournaments,
    racquets
});

export default rootReducer;
