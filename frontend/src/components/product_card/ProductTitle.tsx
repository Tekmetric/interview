interface ProductTitleProps {
  title: string;
  brand?: string;
  as?: 'h2' | 'h3' | 'p';
}

export function ProductTitle({ title, brand, as: Tag = 'h2' }: ProductTitleProps) {
  return (
    <Tag className="m-0 line-clamp-2 text-[0.95rem] font-semibold leading-snug">
      {brand ? `${title} by ${brand}` : title}
    </Tag>
  );
}
