export const LoaderDots = () => {
  return (
    <div className="flex items-center justify-center h-10 gap-2">
      {[0, 1, 2].map((i) => (
        <span
          key={i}
          className="w-3 h-3 rounded-full bg-gray-400 animate-bounce"
          style={{ animationDelay: `${i * 0.15}s` }}
        />
      ))}
    </div>
  );
};
