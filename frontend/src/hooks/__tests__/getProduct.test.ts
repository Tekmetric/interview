import { getProduct } from '../getProduct';
import { HttpError, InvalidResponseError } from '../errors';
import { createMockResponse, sampleProduct } from './fixtures';

describe('getProduct', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    global.fetch = originalFetch;
  });

  it('returns a correctly shaped Product on success', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProduct)
    );

    const result = await getProduct(1);

    expect(result).toEqual(sampleProduct);
    expect(result.id).toBe(1);
    expect(result.price).toBe(9.99);
  });

  it('requests the correct product URL', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProduct)
    );

    await getProduct(42);

    expect(global.fetch).toHaveBeenCalledWith(
      'https://dummyjson.com/products/42',
      expect.objectContaining({ signal: expect.any(AbortSignal) })
    );
  });

  it('throws HttpError on 404', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(null, { ok: false, status: 404 })
    );

    await expect(getProduct(999)).rejects.toThrow(HttpError);
    await expect(getProduct(999)).rejects.toMatchObject({ status: 404 });
  });

  it('throws InvalidResponseError when response shape is invalid', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse({ id: 1, title: 'Incomplete product' })
    );

    await expect(getProduct(1)).rejects.toThrow(InvalidResponseError);
  });
});
