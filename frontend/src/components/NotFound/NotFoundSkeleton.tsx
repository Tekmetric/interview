const NotFoundPageSkeleton = () => {
  return (
    <div className="mx-auto flex h-full w-full max-w-[640px] animate-pulse flex-col items-center justify-center gap-5">
      <div className="h-[64px] w-[64px] shrink-0 rounded-full bg-gray-200"></div>

      <div className="flex w-full flex-col gap-2">
        <div className="h-5 w-full rounded-md bg-gray-200"></div>
        <div className="h-5 w-full rounded-md bg-gray-200"></div>
        <div className="mx-auto h-5 w-3/4 rounded-md bg-gray-200"></div>
      </div>
    </div>
  );
};

export default NotFoundPageSkeleton;
