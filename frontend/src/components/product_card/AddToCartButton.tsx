import { Button } from '../button/Button';

interface AddToCartButtonProps {
  sku: string;
}

export function AddToCartButton({ sku }: AddToCartButtonProps) {
  return (
    <Button
      variant="primary"
      className="mt-auto"
      onClick={() => console.log('Add to cart clicked', { sku })}
    >
      Add to Cart
    </Button>
  );
}
