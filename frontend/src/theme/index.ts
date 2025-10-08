export * from './lightTheme';

declare module 'styled-components' {
  export interface DefaultTheme {
    opacity: {
      background: number;
      backgroundLight: number;
    };
    colors: {
      hover: string;
      active: string;
      scrollbarColor: string;
      scrollbarBackGdColor: string;
      headerColor: string;
      background: string;
      headerText: string;
    },
    spacing: {
      s: string;
      m: string;
      l: string;
      xl: string;
    },
    fontSize: {
      s: string;
      m: string;
    }
  }
}
