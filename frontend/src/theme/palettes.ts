// Color palettes for the two themes.
//
// USER-TUNABLE: these values are meant to be adjusted to taste. The one hard
// rule: every text color must keep a >= 4.5:1 WCAG AA contrast ratio against
// the backgrounds it appears on (verified in the a11y sweep + axe e2e tests).
// `portal` is decorative-only (glows, spinners) and exempt from that rule.

export interface Palette {
  background: string;
  surface: string;
  surfaceHover: string;
  text: string;
  textMuted: string;
  border: string;
  accent: string;
  onAccent: string;
  accentSoft: string;
  focusRing: string;
  statusAlive: string;
  statusDead: string;
  statusUnknown: string;
  portal: string;
}

export const lightPalette: Palette = {
  background: '#ffffff',
  surface: '#f4f6f2',
  surfaceHover: '#e9eee5',
  text: '#171b16',
  textMuted: '#55604f',
  border: '#dce1d8',
  accent: '#2e7d32',
  onAccent: '#ffffff',
  accentSoft: '#e6f2e4',
  focusRing: '#2e7d32',
  statusAlive: '#2e7d32',
  statusDead: '#b3261e',
  statusUnknown: '#5f6368',
  portal: '#97ce4c',
};

export const darkPalette: Palette = {
  background: '#0f1512',
  surface: '#1a221c',
  surfaceHover: '#232d26',
  text: '#e7ece6',
  textMuted: '#a7b3a4',
  border: '#2b352d',
  accent: '#97ce4c',
  onAccent: '#0f1512',
  accentSoft: '#1f2f1c',
  focusRing: '#97ce4c',
  statusAlive: '#97ce4c',
  statusDead: '#ff9088',
  statusUnknown: '#a7b3a4',
  portal: '#97ce4c',
};
