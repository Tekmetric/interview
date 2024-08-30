import { TaskStatus } from './types';

type ColorType = 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';

export const statusColors: Record<string, ColorType> = {
  [TaskStatus.ReadyForWork]: 'default',
  [TaskStatus.InProgress]: 'primary',
  [TaskStatus.Completed]: 'success',
};
