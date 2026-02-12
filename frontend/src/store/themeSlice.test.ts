import themeReducer, { toggleTheme, setTheme, ThemeState } from './themeSlice';

describe('themeSlice', () => {
  const initialState: ThemeState = {
    isDarkMode: false,
  };

  it('should return the initial state', () => {
    expect(themeReducer(undefined, { type: 'unknown' })).toEqual(
      expect.objectContaining({
        isDarkMode: expect.any(Boolean),
      })
    );
  });

  it('should handle toggleTheme', () => {
    const state = themeReducer(initialState, toggleTheme());
    expect(state.isDarkMode).toBe(true);

    const toggledState = themeReducer(state, toggleTheme());
    expect(toggledState.isDarkMode).toBe(false);
  });

  it('should handle setTheme with true', () => {
    const state = themeReducer(initialState, setTheme(true));
    expect(state.isDarkMode).toBe(true);
  });

  it('should handle setTheme with false', () => {
    const darkState: ThemeState = { isDarkMode: true };
    const state = themeReducer(darkState, setTheme(false));
    expect(state.isDarkMode).toBe(false);
  });

  it('should toggle from light to dark', () => {
    const lightState: ThemeState = { isDarkMode: false };
    const darkState = themeReducer(lightState, toggleTheme());
    expect(darkState.isDarkMode).toBe(true);
  });

  it('should toggle from dark to light', () => {
    const darkState: ThemeState = { isDarkMode: true };
    const lightState = themeReducer(darkState, toggleTheme());
    expect(lightState.isDarkMode).toBe(false);
  });
});
