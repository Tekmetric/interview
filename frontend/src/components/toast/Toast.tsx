interface ToastProps {
  message: string;
  onDismiss: () => void;
}

export function Toast({ message, onDismiss }: ToastProps) {
  return (
    <div
      role="status"
      className="relative rounded border border-neutral-200 bg-white px-10 py-2.5 text-base text-neutral-800 shadow-lg"
    >
      <p className="m-0 text-center">{message}</p>
      <button
        type="button"
        onClick={onDismiss}
        className="absolute right-1 top-1/2 inline-flex min-h-9 min-w-9 -translate-y-1/2 cursor-pointer items-center justify-center rounded text-neutral-500 hover:text-neutral-800 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
        aria-label="Dismiss notification"
      >
        ×
      </button>
    </div>
  );
}
