import { getCategories } from '../getCategories';
import {
  HttpError,
  InvalidResponseError,
  NetworkError,
} from '../errors';
import { createMockResponse, sampleCategories } from './fixtures';

describe('getCategories', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    global.fetch = originalFetch;
  });

  it('returns a correctly shaped ProductCategory array on success', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleCategories)
    );

    const result = await getCategories();

    expect(result).toEqual(sampleCategories);
    expect(result).toHaveLength(2);
    expect(result[0]!.name).toBe('Beauty');
  });

  it('requests the categories endpoint', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleCategories)
    );

    await getCategories();

    expect(global.fetch).toHaveBeenCalledWith(
      'https://dummyjson.com/products/categories',
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );
  });

  it('throws HttpError on non-2xx responses', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(null, { ok: false, status: 500 })
    );

    await expect(getCategories()).rejects.toThrow(HttpError);
    await expect(getCategories()).rejects.toMatchObject({ status: 500 });
  });

  it('throws NetworkError when fetch rejects', async () => {
    (global.fetch as jest.Mock).mockRejectedValue(new Error('Failed to fetch'));

    await expect(getCategories()).rejects.toThrow(NetworkError);
  });

  it('throws InvalidResponseError when response shape is invalid', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse([{ slug: 'beauty' }])
    );

    await expect(getCategories()).rejects.toThrow(InvalidResponseError);
    await expect(getCategories()).rejects.toThrow(
      'Response does not match expected shape'
    );
  });
});
