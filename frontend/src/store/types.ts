import { TaskState } from './data/task/types.ts';

export interface State {
  data: {
    tasks: TaskState;
  };
}
