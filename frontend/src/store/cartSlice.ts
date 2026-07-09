import { createSlice, type PayloadAction } from '@reduxjs/toolkit';
import type { AddToCartPayload, CartItem } from './cartTypes';

interface CartState {
  items: CartItem[];
}

const initialState: CartState = {
  items: [],
};

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addItem(state, action: PayloadAction<AddToCartPayload>) {
      const existing = state.items.find(
        (item) => item.sku === action.payload.sku
      );

      if (existing) {
        existing.quantity += 1;
        return;
      }

      state.items.push({ ...action.payload, quantity: 1 });
    },
    removeItem(state, action: PayloadAction<string>) {
      state.items = state.items.filter((item) => item.sku !== action.payload);
    },
    updateQuantity(
      state,
      action: PayloadAction<{ sku: string; quantity: number }>
    ) {
      const item = state.items.find(
        (cartItem) => cartItem.sku === action.payload.sku
      );

      if (!item) {
        return;
      }

      if (action.payload.quantity <= 0) {
        state.items = state.items.filter(
          (cartItem) => cartItem.sku !== action.payload.sku
        );
        return;
      }

      item.quantity = action.payload.quantity;
    },
    clearCart(state) {
      state.items = [];
    },
  },
});

export const { addItem, removeItem, updateQuantity, clearCart } =
  cartSlice.actions;
export default cartSlice.reducer;
