export const Loader = () => {
  return (
    <div className="absolute top-0 bottom-0 right-0 left-0 pointer-events-none">
      <div className="absolute bottom-1/2 right-1/2 translate-x-1/2 translate-y-1/2">
        <div className="animate-spin rounded-full border-4 border-solid p-8 border-primary border-t-transparent"></div>
      </div>
    </div>
  )
}