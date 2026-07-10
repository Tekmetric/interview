interface ProductImageProps {
  src: string;
  alt: string;
  priority?: boolean;
}

const IMAGE_SIZE = 300;

// Could add image crunching here or rendering different size images
// if the backend supported it for performance.
export function ProductImage({ src, alt, priority = false }: ProductImageProps) {
  return (
    <div className="flex aspect-square items-center justify-center bg-hover">
      <img
        className="h-full w-full object-contain"
        src={src}
        alt={alt}
        width={IMAGE_SIZE}
        height={IMAGE_SIZE}
        decoding="async"
        loading={priority ? 'eager' : 'lazy'}
        {...(priority ? { fetchPriority: 'high' as const } : {})}
      />
    </div>
  );
}
