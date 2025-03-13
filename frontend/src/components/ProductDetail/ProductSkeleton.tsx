const ProductSkeleton = () => {
  return (
    <div className="flex w-full max-w-[780px] animate-pulse flex-col space-y-8">
      <div className="w-full">
        <div className="mb-4 h-2.5 w-100 rounded-full bg-gray-200"></div>
        <div className="mb-4 h-2.5 w-full rounded-full bg-gray-200"></div>
      </div>
      <div className="flex w-full max-w-[780px] flex-col justify-evenly gap-10 md:flex-row">
        <div className="flex h-50 w-full items-center justify-center rounded-sm bg-gray-300 sm:w-96 md:h-100 md:min-w-100">
          <svg
            className="h-20 w-20 text-gray-200"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            fill="currentColor"
            viewBox="0 0 20 18"
          >
            <path d="M18 0H2a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2Zm-5.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Zm4.376 10.481A1 1 0 0 1 16 15H4a1 1 0 0 1-.895-1.447l3.5-7A1 1 0 0 1 7.468 6a.965.965 0 0 1 .9.5l2.775 4.757 1.546-1.887a1 1 0 0 1 1.618.1l2.541 4a1 1 0 0 1 .028 1.011Z" />
          </svg>
        </div>
        <div className="flex w-full flex-col gap-3">
          <div className="mb-4 h-2.5 w-48 rounded-full bg-gray-200"></div>
          <div className="mb-2.5 h-2 max-w-[480px] rounded-full bg-gray-200"></div>
          <div className="mb-2.5 h-2 rounded-full bg-gray-200"></div>
          <div className="mb-2.5 h-2 max-w-[440px] rounded-full bg-gray-200"></div>
          <div className="mb-2.5 h-2 max-w-[460px] rounded-full bg-gray-200"></div>
          <div className="h-2 max-w-[360px] rounded-full bg-gray-200"></div>
        </div>
      </div>
    </div>
  );
};

export default ProductSkeleton;
