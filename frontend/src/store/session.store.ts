import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { defaultTheme } from "../constants/theme.constants";
import { Themes } from "../types/Theme";

export interface SessionState {
  username: string | undefined,
  isAuthenticated: boolean,
  theme: Themes,
};

const initialState: SessionState = {
  username: undefined,
  isAuthenticated: false,
  theme: defaultTheme
};

export const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    setTheme: (state, action: PayloadAction<Themes>) => {
      state.theme = action.payload;
    },
    completeLogin: (state, action: PayloadAction<string>) => {
      state.username = action.payload;
      state.isAuthenticated = !!action.payload;
    },
    logout: (state) => {
      state.username = undefined;
      state.isAuthenticated = false;
    }
  }
})

export const { setTheme, completeLogin, logout } = sessionSlice.actions;

export default sessionSlice.reducer
