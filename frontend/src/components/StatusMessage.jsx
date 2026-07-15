import Button from './Button';

// Centered panel for empty/error/info states. tone="error" switches the live
// region from status to alert.
export default function StatusMessage({
  icon,
  title,
  body,
  onRetry,
  retryLabel,
  tone = 'neutral',
}) {
  return (
    <div
      role={tone === 'error' ? 'alert' : 'status'}
      className="flex flex-col items-center gap-3 rounded-xl border border-line bg-surface p-10 text-center"
    >
      {icon && (
        <span className="flex size-12 items-center justify-center rounded-full bg-surface-2 text-muted">
          {icon}
        </span>
      )}
      {title && <p className="font-semibold text-ink">{title}</p>}
      {body && <p className="max-w-prose text-sm text-muted">{body}</p>}
      {onRetry && (
        <Button variant="primary" onClick={onRetry} className="mt-1">
          {retryLabel}
        </Button>
      )}
    </div>
  );
}
