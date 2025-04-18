// Action Type Constants
export const SET_NOTICE = 'SET_NOTICE';
export const CLEAR_NOTICE = 'CLEAR_NOTICE';

// Action Interfaces

// Set Notice
export interface setNoticeAction {
  type: typeof SET_NOTICE;
  payload: NoticeState;
}

// Clear Notice
export interface clearNoticeAction {
  type: typeof CLEAR_NOTICE;
}

// Combine all action interfaces
export type NoticeAction = setNoticeAction | clearNoticeAction;

// App State
export interface NoticeState {
  type: 'success' | 'error' | null;
  message: string;
}

export interface UserState {
  id: number;
  username: string;
  name: string;
  role: string;
  image: string;
}

export interface ApplicationState {
  user: UserState;
  notice: NoticeState;
}
