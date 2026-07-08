interface ProductTitleProps {
  title: string;
  brand?: string;
}

export function ProductTitle({ title, brand }: ProductTitleProps) {
  return (
    <h2 className="m-0 line-clamp-2 text-[0.95rem] font-semibold leading-snug">
      {brand ? `${title} by ${brand}` : title}
    </h2>
  );
}
