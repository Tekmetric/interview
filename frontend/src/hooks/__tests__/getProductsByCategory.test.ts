import { getProductsByCategory } from '../getProductsByCategory';
import { InvalidResponseError } from '../errors';
import {
  createMockResponse,
  sampleProductsResponse,
} from './fixtures';

describe('getProductsByCategory', () => {
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

    const result = await getProductsByCategory({ category: 'beauty' });

    expect(result).toEqual(sampleProductsResponse);
    expect(result.products).toHaveLength(1);
  });

  it('includes the category slug in the request path and uses default limit=12', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponse)
    );

    await getProductsByCategory({ category: 'smartphones' });

    expect(global.fetch).toHaveBeenCalledWith(
      'https://dummyjson.com/products/category/smartphones?limit=12&skip=0',
      expect.any(Object)
    );
  });

  it('forwards custom limit, skip, and sort params', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponse)
    );

    await getProductsByCategory({
      category: 'beauty',
      limit: 5,
      skip: 10,
      sortBy: 'price',
      order: 'asc',
    });

    expect(global.fetch).toHaveBeenCalledWith(
      'https://dummyjson.com/products/category/beauty?limit=5&skip=10&sortBy=price&order=asc',
      expect.any(Object)
    );
  });

  it('throws InvalidResponseError when response shape is invalid', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse({ total: 16, skip: 0, limit: 12 })
    );

    await expect(
      getProductsByCategory({ category: 'beauty' })
    ).rejects.toThrow(InvalidResponseError);
    await expect(
      getProductsByCategory({ category: 'beauty' })
    ).rejects.toThrow('Response does not match expected shape');
  });
});
