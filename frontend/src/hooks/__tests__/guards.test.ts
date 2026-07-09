import {
  isProductCategoriesResponse,
  isProductCategory,
  isProductDetailRaw,
  isProductSummaryRaw,
  isProductsResponseRaw,
} from '../guards';
import {
  sampleCategories,
  sampleProductDetailRaw,
  sampleProductSummaryRaw,
  sampleProductsResponseRaw,
} from './fixtures';

describe('isProductSummaryRaw', () => {
  it('accepts a representative summary product', () => {
    expect(isProductSummaryRaw(sampleProductSummaryRaw)).toBe(true);
  });

  it('rejects when id is missing', () => {
    const { id: _id, ...productWithoutId } = sampleProductSummaryRaw;
    expect(isProductSummaryRaw(productWithoutId)).toBe(false);
  });

  it('rejects when sku is missing', () => {
    const { sku: _sku, ...productWithoutSku } = sampleProductSummaryRaw;
    expect(isProductSummaryRaw(productWithoutSku)).toBe(false);
  });

  it('rejects when title is not a string', () => {
    expect(isProductSummaryRaw({ ...sampleProductSummaryRaw, title: 123 })).toBe(
      false
    );
  });

  it('rejects when price is not a number', () => {
    expect(
      isProductSummaryRaw({ ...sampleProductSummaryRaw, price: '9.99' })
    ).toBe(false);
  });

  it('accepts products without a brand', () => {
    const { brand: _brand, ...productWithoutBrand } = sampleProductSummaryRaw;
    expect(isProductSummaryRaw(productWithoutBrand)).toBe(true);
  });

  it('rejects when reviews is not an array', () => {
    expect(
      isProductSummaryRaw({ ...sampleProductSummaryRaw, reviews: 'invalid' })
    ).toBe(false);
  });

  it('rejects when availabilityStatus is missing', () => {
    const { availabilityStatus: _availabilityStatus, ...productWithoutStatus } =
      sampleProductSummaryRaw;
    expect(isProductSummaryRaw(productWithoutStatus)).toBe(false);
  });

  it('rejects null and non-object values', () => {
    expect(isProductSummaryRaw(null)).toBe(false);
    expect(isProductSummaryRaw('product')).toBe(false);
  });
});

describe('isProductDetailRaw', () => {
  it('accepts a representative detail product', () => {
    expect(isProductDetailRaw(sampleProductDetailRaw)).toBe(true);
  });

  it('rejects when sku is missing', () => {
    const { sku: _sku, ...productWithoutSku } = sampleProductDetailRaw;
    expect(isProductDetailRaw(productWithoutSku)).toBe(false);
  });

  it('rejects when images is not a string array', () => {
    expect(
      isProductDetailRaw({ ...sampleProductDetailRaw, images: ['valid', 1] })
    ).toBe(false);
  });

  it('rejects null and non-object values', () => {
    expect(isProductDetailRaw(null)).toBe(false);
    expect(isProductDetailRaw('product')).toBe(false);
  });
});

describe('isProductsResponseRaw', () => {
  it('accepts a representative products response', () => {
    expect(isProductsResponseRaw(sampleProductsResponseRaw)).toBe(true);
  });

  it('accepts an empty products array', () => {
    expect(
      isProductsResponseRaw({
        products: [],
        total: 0,
        skip: 0,
        limit: 12,
      })
    ).toBe(true);
  });

  it('rejects when products is missing', () => {
    const { products: _products, ...responseWithoutProducts } =
      sampleProductsResponseRaw;
    expect(isProductsResponseRaw(responseWithoutProducts)).toBe(false);
  });

  it('rejects when total is not a number', () => {
    expect(
      isProductsResponseRaw({ ...sampleProductsResponseRaw, total: '194' })
    ).toBe(false);
  });

  it('rejects when a product in the array is malformed', () => {
    expect(
      isProductsResponseRaw({
        ...sampleProductsResponseRaw,
        products: [{ id: 1 }],
      })
    ).toBe(false);
  });
});

describe('isProductCategory', () => {
  it('accepts a representative DummyJSON category', () => {
    expect(isProductCategory(sampleCategories[0])).toBe(true);
  });

  it('rejects when slug is missing', () => {
    const category = sampleCategories[0]!;
    const { slug: _slug, ...categoryWithoutSlug } = category;
    expect(isProductCategory(categoryWithoutSlug)).toBe(false);
  });

  it('rejects when name is not a string', () => {
    expect(isProductCategory({ ...sampleCategories[0]!, name: 123 })).toBe(false);
  });

  it('rejects null and non-object values', () => {
    expect(isProductCategory(null)).toBe(false);
    expect(isProductCategory('beauty')).toBe(false);
  });
});

describe('isProductCategoriesResponse', () => {
  it('accepts a representative DummyJSON categories response', () => {
    expect(isProductCategoriesResponse(sampleCategories)).toBe(true);
  });

  it('accepts an empty array', () => {
    expect(isProductCategoriesResponse([])).toBe(true);
  });

  it('rejects when a category in the array is malformed', () => {
    expect(isProductCategoriesResponse([{ slug: 'beauty' }])).toBe(false);
  });

  it('rejects non-array values', () => {
    expect(isProductCategoriesResponse({ slug: 'beauty' })).toBe(false);
  });
});
