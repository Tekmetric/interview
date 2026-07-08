interface ProductTitleProps {
  title: string;
  brand: string;
}

export function ProductTitle({ title, brand }: ProductTitleProps) {
  return (
    <h2 className="product-card__title">
      {title} by {brand}
    </h2>
  );
}
