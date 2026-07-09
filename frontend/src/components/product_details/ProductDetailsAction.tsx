import { Button } from '../button/Button';

interface ProductDetailsActionProps {
  sku: string;
  inStock: boolean;
}

export function ProductDetailsAction({ sku, inStock }: ProductDetailsActionProps) {
  const label = inStock ? 'Add to Cart' : 'Notify Me';

  return (
    <Button
      variant="primary"
      className="w-full"
      onClick={() => console.log(`${label} clicked`, { sku })}
    >
      {label}
    </Button>
  );
}
