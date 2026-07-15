import { describe, it, expect } from 'vitest';
import { translateMedium } from './mediums';

describe('translateMedium', () => {
  it('translates a known medium', () => {
    expect(translateMedium('Oil on canvas', 'es')).toBe('Óleo sobre lienzo');
    expect(translateMedium('Bronze', 'ja')).toBe('ブロンズ');
  });

  it('is case-insensitive and trims', () => {
    expect(translateMedium('  MARBLE  ', 'fr')).toBe('Marbre');
  });

  it('falls back to the original English for unknown mediums', () => {
    const exotic = 'Walnut with gesso, paint, tin leaf, traces of linen';
    expect(translateMedium(exotic, 'es')).toBe(exotic);
  });

  it('returns English locale and empty values unchanged', () => {
    expect(translateMedium('Oil on canvas', 'en')).toBe('Oil on canvas');
    expect(translateMedium(null, 'es')).toBeNull();
  });
});
