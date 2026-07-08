interface ProductImageProps {
  src: string;
  alt: string;
}

export function ProductImage({ src, alt }: ProductImageProps) {
  return (
    <div className="product-card__image-wrap">
      <img className="product-card__image" src={src} alt={alt} loading="lazy" />
    </div>
  );
}
