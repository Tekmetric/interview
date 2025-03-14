import PaymentSummary from '../../components/CartDetail/PaymentSummary.tsx';
import CartItems from '../../components/CartDetail/CartItems.tsx';
import { useCart } from '../../hooks/useCart.tsx';
import EmptyCart from '../../components/CartDetail/EmptyCart.tsx';

const CartPage = () => {
  const { cart } = useCart();

  if (
    !cart ||
    cart.products.filter((product) => product.quantity > 0).length === 0
  ) {
    return <EmptyCart />;
  }

  return (
    <div className="flex flex-col gap-5">
      <span className="text-xl font-medium">My Cart</span>
      <div className="flex flex-col justify-between gap-5 md:flex-row">
        <CartItems cartItems={cart.products} />
        <PaymentSummary cart={cart} />
      </div>
    </div>
  );
};

export default CartPage;
