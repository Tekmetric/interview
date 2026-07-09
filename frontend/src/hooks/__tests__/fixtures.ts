import type {
  ProductCategory,
  ProductDetail,
  ProductSummary,
  ProductsResponse,
} from '../types';

export const sampleProductSummaryRaw = {
  id: 1,
  sku: 'RCH45Q1A',
  title: 'Essence Mascara Lash Princess',
  price: 9.99,
  discountPercentage: 7.17,
  rating: 4.94,
  brand: 'Essence',
  reviews: [
    {
      rating: 5,
      comment: 'Very satisfied!',
      date: '2024-05-23T08:56:21.618Z',
      reviewerName: 'Scarlett Wright',
      reviewerEmail: 'scarlett.wright@x.dummyjson.com',
    },
  ],
  thumbnail:
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp',
};

export const sampleProductSummary: ProductSummary = {
  id: 1,
  sku: 'RCH45Q1A',
  title: 'Essence Mascara Lash Princess',
  price: 9.99,
  discountPercentage: 7.17,
  rating: 4.94,
  brand: 'Essence',
  reviewCount: 1,
  thumbnail:
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp',
};

export const sampleProductsResponseRaw = {
  products: [sampleProductSummaryRaw],
  total: 194,
  skip: 0,
  limit: 12,
};

export const sampleProductsResponse: ProductsResponse = {
  products: [sampleProductSummary],
  total: 194,
  skip: 0,
  limit: 12,
};

export const sampleProductDetailRaw = {
  id: 1,
  sku: 'RCH45Q1A',
  title: 'Essence Mascara Lash Princess',
  description:
    'The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects.',
  price: 9.99,
  discountPercentage: 7.17,
  rating: 4.94,
  brand: 'Essence',
  stock: 5,
  weight: 2,
  dimensions: {
    width: 23.17,
    height: 14.43,
    depth: 28.01,
  },
  shippingInformation: 'Ships in 1 month',
  availabilityStatus: 'Low Stock',
  returnPolicy: '30 days return policy',
  reviews: [
    {
      rating: 5,
      comment: 'Very satisfied!',
      date: '2024-05-23T08:56:21.618Z',
      reviewerName: 'Scarlett Wright',
      reviewerEmail: 'scarlett.wright@x.dummyjson.com',
    },
  ],
  thumbnail:
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp',
  images: [
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/1.webp',
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/2.webp',
  ],
};

export const sampleProductDetail: ProductDetail = {
  id: 1,
  sku: 'RCH45Q1A',
  title: 'Essence Mascara Lash Princess',
  description:
    'The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects.',
  price: 9.99,
  discountPercentage: 7.17,
  rating: 4.94,
  brand: 'Essence',
  stock: 5,
  weight: 2,
  dimensions: {
    width: 23.17,
    height: 14.43,
    depth: 28.01,
  },
  shippingInformation: 'Ships in 1 month',
  availabilityStatus: 'Low Stock',
  returnPolicy: '30 days return policy',
  reviews: [
    {
      rating: 5,
      comment: 'Very satisfied!',
      date: '2024-05-23T08:56:21.618Z',
      reviewerName: 'Scarlett Wright',
    },
  ],
  thumbnail:
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/thumbnail.webp',
  images: [
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/1.webp',
    'https://cdn.dummyjson.com/product-images/beauty/essence-mascara-lash-princess/2.webp',
  ],
};

export const sampleCategories: ProductCategory[] = [
  {
    slug: 'beauty',
    name: 'Beauty',
    url: 'https://dummyjson.com/products/category/beauty',
  },
  {
    slug: 'smartphones',
    name: 'Smartphones',
    url: 'https://dummyjson.com/products/category/smartphones',
  },
];

export const PRODUCT_SUMMARY_SELECT =
  'id,sku,title,brand,price,discountPercentage,rating,reviews,thumbnail';

export const PRODUCT_DETAIL_SELECT =
  'id,sku,title,description,brand,price,discountPercentage,rating,stock,images,thumbnail,weight,dimensions,shippingInformation,availabilityStatus,returnPolicy,reviews';

export function createMockResponse(
  body: unknown,
  options: { ok?: boolean; status?: number } = {}
): Response {
  const { ok = true, status = 200 } = options;

  return {
    ok,
    status,
    json: jest.fn().mockResolvedValue(body),
  } as unknown as Response;
}

export function withSelectParam(url: string, select: string): string {
  const separator = url.includes('?') ? '&' : '?';
  return `${url}${separator}select=${encodeURIComponent(select)}`;
}
