export const SET_NOTICE = 'SET_NOTICE';
export const CLEAR_NOTICE = 'CLEAR_NOTICE';

export interface UserState {
  id: number;
  username: string;
  name: string;
  role: string;
  image: string;
}

export interface NoticeState {
  type: 'success' | 'error' | null;
  message: string;
}

export interface ApplicationState {
  user: UserState;
  notice: NoticeState;
}
