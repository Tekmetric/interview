import { NoticeState, SET_NOTICE, CLEAR_NOTICE } from './types';

export const setNotice = (notice: NoticeState) => ({
  type: SET_NOTICE,
  payload: notice,
});

export const clearNotice = () => ({
  type: CLEAR_NOTICE,
});
