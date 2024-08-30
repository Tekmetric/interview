import { combineReducers } from 'redux';
import taskSlice from './data/task/taskSlice';

const rootReducer = combineReducers({
  data: combineReducers({
    tasks: taskSlice,
    // more reducers data here
  }),
  // more root reducers here
});

export default rootReducer;
