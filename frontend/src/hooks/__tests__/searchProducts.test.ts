import { searchProducts } from '../searchProducts';
import { InvalidResponseError } from '../errors';
import {
  createMockResponse,
  sampleProductsResponse,
} from './fixtures';

describe('searchProducts', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    global.fetch = originalFetch;
  });

  it('returns a correctly shaped ProductsResponse on success', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponse)
    );

    const result = await searchProducts({ q: 'phone' });

    expect(result).toEqual(sampleProductsResponse);
    expect(result.products).toHaveLength(1);
  });

  it('URL-encodes the search query and uses default limit=12', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponse)
    );

    await searchProducts({ q: 'phone case' });

    expect(global.fetch).toHaveBeenCalledWith(
      'https://dummyjson.com/products/search?q=phone+case&limit=12&skip=0',
      expect.any(Object)
    );
  });

  it('accepts an empty result set as a valid response', async () => {
    const emptyResponse = {
      products: [],
      total: 0,
      skip: 0,
      limit: 12,
    };

    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(emptyResponse)
    );

    const result = await searchProducts({ q: 'nonexistentxyz' });

    expect(result.products).toEqual([]);
    expect(result.total).toBe(0);
  });

  it('throws InvalidResponseError when response shape is invalid', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse({ q: 'phone', total: 0 })
    );

    await expect(searchProducts({ q: 'phone' })).rejects.toThrow(
      InvalidResponseError
    );
  });
});
