const ProductsSkeleton = () => {
  return (
    <div className="flex h-full flex-col gap-5">
      <div className="mx-auto h-12 w-full animate-pulse rounded-md bg-gray-200"></div>

      <ProductsGridSkeleton />
    </div>
  );
};

export const ProductsGridSkeleton = () => {
  return (
    <div className="flex-grow">
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-4">
        {Array.from({ length: 8 }).map((_, index) => (
          <ProductItemSkeleton key={index} />
        ))}
      </div>
    </div>
  );
};

const ProductItemSkeleton = () => {
  return (
    <div className="flex w-full animate-pulse flex-col gap-2">
      <div className="relative flex h-50 items-center justify-center border border-gray-300 bg-gray-200">
        <svg
          className="h-12 w-12 text-gray-300"
          aria-hidden="true"
          xmlns="http://www.w3.org/2000/svg"
          fill="currentColor"
          viewBox="0 0 20 18"
        >
          <path d="M18 0H2a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2Zm-5.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Zm4.376 10.481A1 1 0 0 1 16 15H4a1 1 0 0 1-.895-1.447l3.5-7A1 1 0 0 1 7.468 6a.965.965 0 0 1 .9.5l2.775 4.757 1.546-1.887a1 1 0 0 1 1.618.1l2.541 4a1 1 0 0 1 .028 1.011Z" />
        </svg>
      </div>

      <div className="flex flex-col gap-1">
        <div className="h-5 w-3/4 rounded-md bg-gray-200"></div>

        <div className="flex items-center gap-2">
          <div className="flex">
            {[1, 2, 3, 4, 5].map((star) => (
              <div
                key={star}
                className="mr-1 h-4 w-4 rounded-full bg-gray-200"
              ></div>
            ))}
          </div>
          <div className="h-3 w-10 rounded-full bg-gray-200"></div>
        </div>

        <div className="h-4 w-16 rounded-md bg-gray-200"></div>
      </div>
    </div>
  );
};

export default ProductsSkeleton;
