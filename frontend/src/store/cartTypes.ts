/** Snapshot of product fields needed to render a cart line item. */
export interface CartItem {
  sku: string;
  title: string;
  price: number;
  discountPercentage: number;
  thumbnail: string;
  quantity: number;
}

export type AddToCartPayload = Omit<CartItem, 'quantity'>;
