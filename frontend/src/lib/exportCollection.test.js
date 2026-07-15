import { describe, it, expect } from 'vitest';
import { collectionToCsv } from './exportCollection';

describe('collectionToCsv', () => {
  it('writes a header row plus one row per item', () => {
    const csv = collectionToCsv([
      {
        title: 'Sunflowers',
        artist: 'Vincent van Gogh',
        date: '1887',
        medium: 'Oil on canvas',
        department: 'European Paintings',
        culture: null,
        isPublicDomain: true,
        url: 'https://www.metmuseum.org/x/1',
        image: 'https://images.metmuseum.org/1.jpg',
      },
    ]);
    const lines = csv.split('\n');
    expect(lines).toHaveLength(2);
    expect(lines[0]).toBe(
      'Title,Artist,Date,Medium,Department,Culture,Public domain,Met URL,Image URL'
    );
    expect(lines[1]).toContain('Sunflowers');
    expect(lines[1]).toContain('Yes');
    expect(lines[1]).toContain('https://images.metmuseum.org/1.jpg');
  });

  it('quotes and escapes fields with commas or quotes', () => {
    const csv = collectionToCsv([
      { title: 'Portrait, with "quotes"', isPublicDomain: false },
    ]);
    const row = csv.split('\n')[1];
    expect(row.startsWith('"Portrait, with ""quotes"""')).toBe(true);
    expect(row).toContain(',No,'); // isPublicDomain false + empty neighbors
  });

  it('renders missing values as empty cells', () => {
    const csv = collectionToCsv([{ title: 'Untitled work' }]);
    const row = csv.split('\n')[1];
    expect(row).toBe('Untitled work,,,,,,No,,');
  });
});
