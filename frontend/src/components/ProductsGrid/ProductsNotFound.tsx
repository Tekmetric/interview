import { PackageSearch } from 'lucide-react';

const ProductsNotFound = () => {
  return (
    <div className="mx-auto flex h-full w-full max-w-[640px] flex-col items-center justify-center gap-5">
      <PackageSearch className="h-[64px] w-[64px] shrink-0 text-gray-500" />
      <span className="text-center text-lg text-gray-500">
        We're sorry, your search had no results. Check if you spelled it
        correctly or try searching using another term.
      </span>
    </div>
  );
};

export default ProductsNotFound;
