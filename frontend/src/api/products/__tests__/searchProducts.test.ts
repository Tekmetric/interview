import { searchProducts } from '../searchProducts';
import { InvalidResponseError } from '../errors';
import {
  createMockResponse,
  PRODUCT_SUMMARY_SELECT,
  sampleProductsResponse,
  sampleProductsResponseRaw,
  withSelectParam,
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
      createMockResponse(sampleProductsResponseRaw)
    );

    const result = await searchProducts({ q: 'phone' });

    expect(result).toEqual(sampleProductsResponse);
    expect(result.products).toHaveLength(1);
  });

  it('URL-encodes the search query and uses default limit=12 with select param', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponseRaw)
    );

    await searchProducts({ q: 'phone case' });

    expect(global.fetch).toHaveBeenCalledWith(
      withSelectParam(
        'https://dummyjson.com/products/search?q=phone+case&limit=12&skip=0',
        PRODUCT_SUMMARY_SELECT
      ),
      expect.any(Object)
    );
  });

  it('forwards sortBy and order when provided', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponseRaw)
    );

    await searchProducts({
      q: 'phone',
      sortBy: 'price',
      order: 'asc',
    });

    expect(global.fetch).toHaveBeenCalledWith(
      withSelectParam(
        'https://dummyjson.com/products/search?q=phone&limit=12&skip=0&sortBy=price&order=asc',
        PRODUCT_SUMMARY_SELECT
      ),
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
