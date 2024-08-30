import { TaskStatus } from './types';
import { TFunction } from 'i18next';

type ColorType = 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning';

export const getTranslatedStatus = (t: TFunction): Record<string, ColorType> => ({
  [TaskStatus.ReadyForWork]: t('app.translation.task.status.ReadyForWork'),
  [TaskStatus.InProgress]: t('app.translation.task.status.InProgress'),
  [TaskStatus.Completed]: t('app.translation.task.status.Completed'),
});
