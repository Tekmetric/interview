import { getProduct } from '../getProduct';
import { HttpError, InvalidResponseError } from '../errors';
import {
  createMockResponse,
  PRODUCT_DETAIL_SELECT,
  sampleProductDetail,
  sampleProductDetailRaw,
  withSelectParam,
} from './fixtures';

describe('getProduct', () => {
  const originalFetch = global.fetch;

  beforeEach(() => {
    global.fetch = jest.fn();
  });

  afterEach(() => {
    global.fetch = originalFetch;
  });

  it('returns a correctly shaped ProductDetail on success', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductDetailRaw)
    );

    const result = await getProduct(1);

    expect(result).toEqual(sampleProductDetail);
    expect(result.id).toBe(1);
    expect(result.price).toBe(9.99);
    expect(result.reviews[0]).not.toHaveProperty('reviewerEmail');
  });

  it('requests the correct product URL with select param', async () => {
    (global.fetch as jest.Mock).mockResolvedValue(
      createMockResponse(sampleProductDetailRaw)
    );

    await getProduct(42);

    expect(global.fetch).toHaveBeenCalledWith(
      withSelectParam('https://dummyjson.com/products/42', PRODUCT_DETAIL_SELECT),
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
