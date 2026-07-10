interface ProductImageProps {
  src: string;
  alt: string;
}

// Could add image crunching here or rendering different size images
// if the backend supported it for performance.
export function ProductImage({ src, alt }: ProductImageProps) {
  return (
    <div className="flex aspect-square items-center justify-center bg-hover">
      <img
        className="h-full w-full object-contain"
        src={src}
        alt={alt}
        loading="lazy"
      />
    </div>
  );
}
