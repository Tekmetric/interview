import { getStarStates, roundToNearestHalf } from '../ratingUtils';

describe('roundToNearestHalf', () => {
  it('rounds to the nearest half star', () => {
    expect(roundToNearestHalf(4.2)).toBe(4);
    expect(roundToNearestHalf(4.3)).toBe(4.5);
    expect(roundToNearestHalf(4.7)).toBe(4.5);
    expect(roundToNearestHalf(4.8)).toBe(5);
  });

  it('clamps below 0 and above 5', () => {
    expect(roundToNearestHalf(-1)).toBe(0);
    expect(roundToNearestHalf(6)).toBe(5);
  });
});

describe('getStarStates', () => {
  it('returns four full and one empty for 4.0', () => {
    expect(getStarStates(4.0)).toEqual([
      'full',
      'full',
      'full',
      'full',
      'empty',
    ]);
  });

  it('returns four full and one half for 4.5', () => {
    expect(getStarStates(4.5)).toEqual([
      'full',
      'full',
      'full',
      'full',
      'half',
    ]);
  });

  it('returns five empty for 0', () => {
    expect(getStarStates(0)).toEqual([
      'empty',
      'empty',
      'empty',
      'empty',
      'empty',
    ]);
  });
});
