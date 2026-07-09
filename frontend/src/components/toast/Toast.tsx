interface ToastProps {
  message: string;
  onDismiss: () => void;
}

export function Toast({ message, onDismiss }: ToastProps) {
  return (
    <div
      role="status"
      className="flex items-start gap-3 rounded border border-neutral-200 bg-white px-4 py-3 text-sm text-neutral-800 shadow-lg"
    >
      <p className="m-0 flex-1">{message}</p>
      <button
        type="button"
        onClick={onDismiss}
        className="inline-flex min-h-11 min-w-11 shrink-0 cursor-pointer items-center justify-center rounded px-2 text-neutral-500 hover:text-neutral-800 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
        aria-label="Dismiss notification"
      >
        ×
      </button>
    </div>
  );
}
