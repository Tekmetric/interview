const CartSkeleton = () => {
  return (
    <div className="flex flex-col gap-5">
      <div className="h-7 w-24 rounded-md bg-gray-200"></div>

      <div className="flex flex-col justify-between gap-5 md:flex-row">
        <div className="flex w-full flex-col gap-5">
          {[1, 2, 3].map((item) => (
            <div
              key={item}
              className="flex items-center gap-5 rounded-lg bg-white p-2 md:p-4"
            >
              <div className="h-20 w-20 rounded-md bg-gray-200"></div>
              <div className="h-5 w-32 rounded-full bg-gray-200"></div>
              <div className="ml-auto flex flex-col items-end gap-4">
                <div className="h-6 w-16 rounded-full bg-gray-200"></div>
                <div className="flex items-center gap-2.5">
                  <div className="h-7.5 w-7.5 rounded-full bg-gray-300"></div>
                  <div className="h-5 w-5 rounded-full bg-gray-200"></div>
                  <div className="h-7.5 w-7.5 rounded-full bg-gray-300"></div>
                </div>
                <div className="h-4 w-16 rounded-full bg-gray-200"></div>
              </div>
            </div>
          ))}
        </div>

        <div className="flex h-fit w-full max-w-full flex-col gap-4 rounded-lg bg-white p-2 md:max-w-100 md:p-4">
          <div className="h-6 w-40 rounded-full bg-gray-200"></div>
          <div className="flex flex-col gap-3">
            <div className="flex justify-between border-b border-b-neutral-300 pb-2">
              <div className="h-5 w-28 rounded-full bg-gray-200"></div>
              <div className="h-5 w-16 rounded-full bg-gray-200"></div>
            </div>
            <div className="flex justify-between border-b border-b-neutral-300 pb-2">
              <div className="h-5 w-24 rounded-full bg-gray-200"></div>
              <div className="h-5 w-12 rounded-full bg-gray-200"></div>
            </div>
            <div className="flex justify-between">
              <div className="h-6 w-16 rounded-full bg-gray-200"></div>
              <div className="h-6 w-20 rounded-full bg-gray-200"></div>
            </div>
          </div>
          <div className="h-10 w-full rounded-md bg-gray-300"></div>
        </div>
      </div>
    </div>
  );
};

export default CartSkeleton;
