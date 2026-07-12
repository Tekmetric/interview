// Design tokens shared by both themes. Colors live in light.ts / dark.ts.
export const tokens = {
  font: {
    // System stack: zero font download, native rendering on every OS.
    family: "system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
    size: {
      sm: '0.875rem',
      md: '1rem',
      lg: '1.25rem',
      xl: '1.5rem',
      xxl: '2rem',
    },
    weight: {
      regular: 400,
      medium: 500,
      bold: 700,
    },
  },
  space: {
    xs: '0.25rem',
    sm: '0.5rem',
    md: '1rem',
    lg: '1.5rem',
    xl: '2rem',
    xxl: '3rem',
  },
  radius: {
    sm: '6px',
    md: '10px',
    lg: '16px',
    pill: '999px',
  },
  breakpoint: {
    sm: '480px',
    md: '768px',
    lg: '1024px',
  },
  transition: {
    fast: '150ms ease',
    normal: '250ms ease',
  },
  maxContentWidth: '1200px',
} as const;

export type Tokens = typeof tokens;
