import { isProduct, isProductCategoriesResponse, isProductCategory, isProductsResponse } from '../guards';
import { sampleCategories, sampleProduct, sampleProductsResponse } from './fixtures';

describe('isProduct', () => {
  it('accepts a representative DummyJSON product', () => {
    expect(isProduct(sampleProduct)).toBe(true);
  });

  it('rejects when id is missing', () => {
    const { id: _id, ...productWithoutId } = sampleProduct;
    expect(isProduct(productWithoutId)).toBe(false);
  });

  it('rejects when title is not a string', () => {
    expect(isProduct({ ...sampleProduct, title: 123 })).toBe(false);
  });

  it('rejects when price is not a number', () => {
    expect(isProduct({ ...sampleProduct, price: '9.99' })).toBe(false);
  });

  it('accepts products without a brand', () => {
    const { brand: _brand, ...productWithoutBrand } = sampleProduct;
    expect(isProduct(productWithoutBrand)).toBe(true);
  });

  it('rejects when images is not a string array', () => {
    expect(isProduct({ ...sampleProduct, images: ['valid', 1] })).toBe(false);
  });

  it('rejects null and non-object values', () => {
    expect(isProduct(null)).toBe(false);
    expect(isProduct('product')).toBe(false);
  });
});

describe('isProductsResponse', () => {
  it('accepts a representative DummyJSON products response', () => {
    expect(isProductsResponse(sampleProductsResponse)).toBe(true);
  });

  it('accepts an empty products array', () => {
    expect(
      isProductsResponse({
        products: [],
        total: 0,
        skip: 0,
        limit: 12,
      })
    ).toBe(true);
  });

  it('rejects when products is missing', () => {
    const { products: _products, ...responseWithoutProducts } =
      sampleProductsResponse;
    expect(isProductsResponse(responseWithoutProducts)).toBe(false);
  });

  it('rejects when total is not a number', () => {
    expect(
      isProductsResponse({ ...sampleProductsResponse, total: '194' })
    ).toBe(false);
  });

  it('rejects when a product in the array is malformed', () => {
    expect(
      isProductsResponse({
        ...sampleProductsResponse,
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
    const { slug: _slug, ...categoryWithoutSlug } = sampleCategories[0];
    expect(isProductCategory(categoryWithoutSlug)).toBe(false);
  });

  it('rejects when name is not a string', () => {
    expect(isProductCategory({ ...sampleCategories[0], name: 123 })).toBe(false);
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
