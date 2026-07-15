import { describe, it, expect } from 'vitest';
import { normalizeObject } from './metMuseum';

describe('normalizeObject', () => {
  it('maps the fields the UI needs', () => {
    const raw = {
      objectID: 436524,
      title: 'Sunflowers',
      artistDisplayName: 'Vincent van Gogh',
      objectDate: '1887',
      medium: 'Oil on canvas',
      department: 'European Paintings',
      primaryImageSmall: 'https://images.metmuseum.org/small.jpg',
      primaryImage: 'https://images.metmuseum.org/large.jpg',
      isPublicDomain: true,
      objectURL: 'https://www.metmuseum.org/art/collection/search/436524',
    };
    const art = normalizeObject(raw);
    expect(art.id).toBe(436524);
    expect(art.title).toBe('Sunflowers');
    expect(art.artist).toBe('Vincent van Gogh');
    expect(art.image).toBe('https://images.metmuseum.org/large.jpg');
    expect(art.isPublicDomain).toBe(true);
  });

  it('turns blank strings into null', () => {
    const art = normalizeObject({ objectID: 1, title: '', artistDisplayName: '  ' });
    expect(art.title).toBeNull();
    expect(art.artist).toBeNull();
    expect(art.isPublicDomain).toBe(false);
  });

  it('falls back to the small image when there is no primary image', () => {
    const art = normalizeObject({ objectID: 2, primaryImageSmall: 'small.jpg' });
    expect(art.image).toBe('small.jpg');
    expect(art.thumbnail).toBe('small.jpg');
  });
});
