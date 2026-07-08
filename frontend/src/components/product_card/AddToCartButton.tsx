import { CardButton } from './CardButton';

interface AddToCartButtonProps {
  sku: string;
}

export function AddToCartButton({ sku }: AddToCartButtonProps) {
  return (
    <CardButton
      variant="primary"
      onClick={() => console.log('Add to cart clicked', { sku })}
    >
      Add to Cart
    </CardButton>
  );
}
