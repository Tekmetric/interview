import { getSortedProducts } from '../getSortedProducts';
import {
  HttpError,
  InvalidResponseError,
  NetworkError,
  TimeoutError,
} from '../errors';
import {
  createMockResponse,
  PRODUCT_SUMMARY_SELECT,
  sampleProductsResponse,
  sampleProductsResponseRaw,
  withSelectParam,
} from './fixtures';

describe('getSortedProducts', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    global.fetch = originalFetch;
    jest.useRealTimers();
  });

  it('returns a correctly shaped ProductsResponse on success', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponseRaw)
    );

    const result = await getSortedProducts({
      sortBy: 'price',
      order: 'asc',
    });

    expect(result).toEqual(sampleProductsResponse);
    expect(result.products).toHaveLength(1);
    expect(result.products[0].title).toBe('Essence Mascara Lash Princess');
  });

  it('uses limit=12 and skip=0 by default with select param', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponseRaw)
    );

    await getSortedProducts({ sortBy: 'price', order: 'asc' });

    expect(global.fetch).toHaveBeenCalledWith(
      withSelectParam(
        'https://dummyjson.com/products?sortBy=price&order=asc&limit=12&skip=0',
        PRODUCT_SUMMARY_SELECT
      ),
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );
  });

  it('forwards custom limit and skip to the request URL', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductsResponseRaw)
    );

    await getSortedProducts({
      sortBy: 'rating',
      order: 'desc',
      limit: 5,
      skip: 10,
    });

    expect(global.fetch).toHaveBeenCalledWith(
      withSelectParam(
        'https://dummyjson.com/products?sortBy=rating&order=desc&limit=5&skip=10',
        PRODUCT_SUMMARY_SELECT
      ),
      expect.any(Object)
    );
  });

  it('throws HttpError on non-2xx responses', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(null, { ok: false, status: 500 })
    );

    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toThrow(HttpError);
    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toMatchObject({ status: 500 });
  });

  it('throws NetworkError when fetch rejects', async () => {
    (global.fetch as jest.Mock).mockRejectedValue(new Error('Failed to fetch'));

    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toThrow(NetworkError);
  });

  it('throws TimeoutError when the request is aborted', async () => {
    jest.useFakeTimers();

    (global.fetch as jest.Mock).mockImplementation(
      (_url: string, options: RequestInit) =>
        new Promise((_resolve, reject) => {
          options.signal?.addEventListener('abort', () => {
            reject(new DOMException('The operation was aborted.', 'AbortError'));
          });
        })
    );

    const promise = getSortedProducts({ sortBy: 'price', order: 'asc' });

    jest.advanceTimersByTime(8000);

    await expect(promise).rejects.toThrow(TimeoutError);
  });

  it('throws InvalidResponseError when response is not valid JSON', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      status: 200,
      json: jest.fn().mockRejectedValue(new SyntaxError('Unexpected token')),
    });

    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toThrow(InvalidResponseError);
    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toThrow('Response is not valid JSON');
  });

  it('throws InvalidResponseError when response shape is invalid', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse({ total: 194, skip: 0, limit: 12 })
    );

    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toThrow(InvalidResponseError);
    await expect(
      getSortedProducts({ sortBy: 'price', order: 'asc' })
    ).rejects.toThrow('Response does not match expected shape');
  });
});
