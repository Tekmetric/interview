import { ShoppingCart } from 'lucide-react';

const EmptyCart = () => {
  return (
    <div className="mx-auto flex h-full w-full max-w-[640px] flex-col items-center justify-center gap-5">
      <ShoppingCart className="h-[64px] w-[64px] shrink-0 text-gray-500" />
      <span className="text-center text-lg text-gray-500">
        There are no products in the shopping cart. Add the desired products and
        come back.
      </span>
    </div>
  );
};

export default EmptyCart;
