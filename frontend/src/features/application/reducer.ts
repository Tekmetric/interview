import { ApplicationState, SET_NOTICE, CLEAR_NOTICE, NoticeAction } from './types';

const initialState: ApplicationState = {
  notice: {
    type: null,
    message: '',
  },
  user: {
    id: 1,
    username: 'eric',
    name: 'Eric Callan',
    image: 'https://ericcallantemp.s3.us-east-2.amazonaws.com/profiles/profile_ericcallan.jpeg',
    role: 'candidate',
  },
};

export const applicationReducer = (
  state = initialState,
  action: NoticeAction
): ApplicationState => {
  switch (action.type) {
    case SET_NOTICE:
      return {
        user: { ...state.user },
        notice: action.payload,
      };
    case CLEAR_NOTICE:
      return {
        user: { ...state.user },
        notice: initialState.notice,
      };
    default:
      return state;
  }
};

export default applicationReducer;
