import cartReducer, {
  addItem,
  clearCart,
  removeItem,
  updateQuantity,
} from '../cartSlice';

const sampleItem = {
  sku: 'SKU-1',
  title: 'Test Product',
  price: 19.99,
  discountPercentage: 10,
  thumbnail: 'https://example.com/image.jpg',
};

describe('cartSlice', () => {
  it('adds a new item with quantity 1', () => {
    const state = cartReducer(undefined, addItem(sampleItem));

    expect(state.items).toEqual([{ ...sampleItem, quantity: 1 }]);
  });

  it('increments quantity when adding the same sku again', () => {
    const firstState = cartReducer(undefined, addItem(sampleItem));
    const secondState = cartReducer(firstState, addItem(sampleItem));

    expect(secondState.items).toEqual([{ ...sampleItem, quantity: 2 }]);
  });

  it('removes an item by sku', () => {
    const withItem = cartReducer(undefined, addItem(sampleItem));
    const nextState = cartReducer(withItem, removeItem(sampleItem.sku));

    expect(nextState.items).toEqual([]);
  });

  it('updates quantity and removes the item when quantity is zero', () => {
    const withItem = cartReducer(undefined, addItem(sampleItem));
    const updated = cartReducer(
      withItem,
      updateQuantity({ sku: sampleItem.sku, quantity: 3 })
    );
    const cleared = cartReducer(
      updated,
      updateQuantity({ sku: sampleItem.sku, quantity: 0 })
    );

    expect(updated.items[0]?.quantity).toBe(3);
    expect(cleared.items).toEqual([]);
  });

  it('clears all cart items', () => {
    const withItem = cartReducer(undefined, addItem(sampleItem));
    const nextState = cartReducer(withItem, clearCart());

    expect(nextState.items).toEqual([]);
  });
});
