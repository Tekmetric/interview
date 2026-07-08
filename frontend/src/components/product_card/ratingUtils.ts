export type StarState = 'full' | 'half' | 'empty';

// Converts a decimal rating to a format we can use for star ratings,
// incremented by half-stars.

export function roundToNearestHalf(rating: number): number {
  const clamped = Math.min(5, Math.max(0, rating));
  return Math.round(clamped * 2) / 2;
}

export function getStarStates(rating: number): StarState[] {
  const rounded = roundToNearestHalf(rating);
  const states: StarState[] = [];

  for (let i = 1; i <= 5; i++) {
    if (rounded >= i) {
      states.push('full');
    } else if (rounded >= i - 0.5) {
      states.push('half');
    } else {
      states.push('empty');
    }
  }

  return states;
}
