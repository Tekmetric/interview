import { createSelector } from '@reduxjs/toolkit';
import type { RootState } from './index';

export const selectCartItems = (state: RootState) => state.cart.items;

export const selectCartItemCount = createSelector(selectCartItems, (items) =>
  items.reduce((total, item) => total + item.quantity, 0)
);

export const selectCartSubtotal = createSelector(selectCartItems, (items) =>
  items.reduce((total, item) => total + item.price * item.quantity, 0)
);
