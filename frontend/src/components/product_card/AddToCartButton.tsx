interface AddToCartButtonProps {
  sku: string;
}

export function AddToCartButton({ sku }: AddToCartButtonProps) {
  return (
    <button
      type="button"
      className="product-card__button product-card__button--primary"
      onClick={() => console.log('Add to cart clicked', { sku })}
    >
      Add to Cart
    </button>
  );
}
