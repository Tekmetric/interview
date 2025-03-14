const ProductSkeleton = () => {
  return (
    <div className="flex flex-col gap-4">
      <div className="h-7 w-3/4 rounded-md bg-gray-200 md:h-8"></div>
      <div className="h-12 w-full rounded-md bg-gray-200 lg:w-[75%]"></div>

      <div className="flex max-w-[780px] flex-col justify-evenly gap-10 md:flex-row">
        <div className="relative flex shrink-0 items-center justify-center border border-gray-300 bg-white md:min-w-100">
          <div className="h-50 w-full bg-gray-200 md:h-100">
            <svg
              className="mx-auto my-14 h-20 w-20 text-gray-300 md:my-40"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="currentColor"
              viewBox="0 0 20 18"
            >
              <path d="M18 0H2a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2Zm-5.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Zm4.376 10.481A1 1 0 0 1 16 15H4a1 1 0 0 1-.895-1.447l3.5-7A1 1 0 0 1 7.468 6a.965.965 0 0 1 .9.5l2.775 4.757 1.546-1.887a1 1 0 0 1 1.618.1l2.541 4a1 1 0 0 1 .028 1.011Z" />
            </svg>
          </div>
        </div>

        <div className="flex flex-col gap-3">
          <div className="flex items-center gap-2">
            <div className="flex">
              {[1, 2, 3, 4, 5].map((star) => (
                <div
                  key={star}
                  className="mr-1 h-5 w-5 rounded-full bg-gray-200"
                ></div>
              ))}
            </div>
            <div className="h-4 w-16 rounded-full bg-gray-200"></div>
          </div>

          <div className="h-5 w-24 rounded-full bg-gray-200"></div>

          <div className="my-5 flex flex-col md:my-auto">
            <div className="mb-3 h-12 w-32 rounded-md bg-gray-200"></div>
            <div className="h-10 w-full rounded-md bg-gray-300"></div>
          </div>

          <div className="mt-auto h-5 w-48 rounded-full bg-gray-200"></div>
        </div>
      </div>
    </div>
  );
};

export default ProductSkeleton;
