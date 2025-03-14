import { CartProduct, UpdateCartItemsPayload } from '../../types/Cart.ts';
import { useCart, useUpdateCart } from '../../hooks/useCart.tsx';
import Image from '../Image/Image.tsx';
import { NumericFormat } from 'react-number-format';
import { Minus, Plus } from 'lucide-react';
import { twMerge } from 'tailwind-merge';

interface CartItemsProps {
  cartItems: CartProduct[];
}

const CartItems = ({ cartItems }: CartItemsProps) => {
  const { mutate } = useUpdateCart();

  return (
    <div className="flex w-full flex-col gap-5">
      {cartItems.map((item) => (
        <CartItem key={item.id} item={item} onUpdateCart={mutate} />
      ))}
    </div>
  );
};

interface CartItemProps {
  item: CartProduct;
  onUpdateCart: (data: UpdateCartItemsPayload) => void;
}

const CartItem = ({ item, onUpdateCart }: CartItemProps) => {
  const { cart } = useCart();
  const updateQuantity = (value: number) => {
    onUpdateCart({
      userId: 77,
      products: [
        // due to  how dummyjson api handles mock carts we need to provide previous items too
        ...cart.products
          .filter((product) => product.id !== item.id)
          .map((product) => ({
            id: product.id,
            quantity: product.quantity,
          })),
        {
          id: item.id,
          quantity: value,
        },
      ],
    });
  };

  if (!item.quantity) {
    return null;
  }

  return (
    <div className="flex items-center gap-5 rounded-lg bg-white p-2 md:p-4">
      <div className="relative">
        <Image src={item.thumbnail} alt={item.title} className="h-20 w-20" />
      </div>
      <span className="text-bs font-medium md:text-lg">{item.title}</span>
      <div className="ml-auto flex flex-col items-end gap-4">
        <span className="text-lg font-medium">
          <NumericFormat
            value={item.price}
            displayType="text"
            thousandSeparator
            decimalSeparator="."
            decimalScale={2}
            fixedDecimalScale
            prefix="$"
          />
        </span>
        <div className="flex items-center gap-2.5">
          <button
            className={twMerge(
              'flex h-7.5 w-7.5 cursor-pointer items-center justify-center rounded-full bg-gray-200',
              item.quantity === 1 && 'pointer-events-none bg-gray-100'
            )}
            onClick={() => updateQuantity(item.quantity - 1)}
          >
            <Minus
              className={twMerge('w-4', item.quantity === 1 && 'text-gray-300')}
            />
          </button>
          <span className="text-lg">{item.quantity}</span>
          <button
            className="flex h-7.5 w-7.5 cursor-pointer items-center justify-center rounded-full bg-gray-200"
            onClick={() => updateQuantity(item.quantity + 1)}
          >
            <Plus className="w-4" />
          </button>
        </div>
        <button
          className="flex cursor-pointer text-sm text-neutral-500"
          onClick={() => updateQuantity(0)}
        >
          Remove
        </button>
      </div>
    </div>
  );
};

export default CartItems;
