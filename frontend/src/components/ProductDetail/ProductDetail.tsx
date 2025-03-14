import { useNavigate } from 'react-router';

import { useCart, useUpdateCart } from '../../hooks/useCart.tsx';
import { Product } from '../../types/Product.ts';
import Rating from '../Rating/Rating.tsx';
import Button from '../Button/Button.tsx';
import Image from '../Image/Image.tsx';

import { twMerge } from 'tailwind-merge';

const getAvailabilityStatusColor = (availabilityStatus: string) => {
  switch (availabilityStatus) {
    case 'In Stock':
      return 'text-green-500';
    case 'Out of Stock':
      return 'text-red-500';
    default:
      return 'text-orange-400';
  }
};

interface ProductDetailProps {
  product: Product;
}

const ProductDetail = ({ product }: ProductDetailProps) => {
  const { cart } = useCart();
  const navigate = useNavigate();
  const { mutate: updateCart } = useUpdateCart();

  const handleAddToCart = async () => {
    const previousQuantity =
      cart.products.find((item) => item.id === product.id)?.quantity ?? 0;

    updateCart(
      {
        userId: 77,
        products: [
          // due to  how dummyjson api handles mock carts we need to provide previous items too
          ...cart.products.map((product) => ({
            id: product.id,
            quantity: product.quantity,
          })),
          {
            id: product.id,
            quantity: previousQuantity + 1,
          },
        ],
      },
      {
        onSuccess: () => {
          navigate('/cart');
        },
      }
    );
  };

  return (
    <div className="flex flex-col gap-4">
      <span className="text-xl font-semibold text-neutral-800 md:text-2xl">
        {product.title}
      </span>
      <span className="w-full text-sm text-neutral-500 md:text-base lg:w-[75%]">
        {product.description}
      </span>
      <div className="flex max-w-[780px] flex-col justify-evenly gap-10 md:flex-row">
        <div className="relative flex shrink-0 items-center justify-center border border-gray-300 bg-white md:min-w-100">
          <Image
            src={product.thumbnail}
            alt={product.title}
            className="h-50 w-full object-contain md:h-100"
          />
        </div>
        <div className="flex flex-col gap-3">
          <Rating
            rating={product.rating}
            reviewsCount={product.reviews?.length}
            showRatingValue={true}
          />
          {product.availabilityStatus && (
            <span
              className={twMerge(
                'text-sm',
                getAvailabilityStatusColor(product.availabilityStatus)
              )}
            >
              {product.availabilityStatus}
            </span>
          )}
          <div className="my-5 flex flex-col md:my-auto">
            <span className="text-[48px] text-green-500">${product.price}</span>
            <Button onClick={handleAddToCart}>Add to cart</Button>
          </div>
          {product.returnPolicy && (
            <span className="mt-auto text-base text-neutral-400">
              {product.returnPolicy}
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
