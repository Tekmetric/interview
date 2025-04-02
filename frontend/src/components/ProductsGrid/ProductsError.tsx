import { CloudAlert } from 'lucide-react';

const ProductsError = () => {
  return (
    <div className="mx-auto flex h-full w-full max-w-[640px] flex-col items-center justify-center gap-5">
      <CloudAlert className="h-[64px] w-[64px] shrink-0 text-gray-500" />
      <span className="text-center text-lg text-gray-500">
        Oops! Something went wrong. Please try again later.
      </span>
    </div>
  );
};

export default ProductsError;
