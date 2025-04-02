export interface Cart {
  id: number;
  products: CartProduct[];
  discountedTotal: number;
  total: number;
  totalProducts: number;
  totalQuantity: number;
  userId?: number;
}

export interface CartProduct {
  id: number;
  title: string;
  price: number;
  quantity: number;
  total: number;
  discountedPercentage: number;
  discountedTotal: number;
  thumbnail: string;
}

export interface UpdateCartItemPayload {
  id: number;
  quantity: number;
}

export interface UpdateCartItemsPayload {
  userId: number;
  products: UpdateCartItemPayload[];
}
