import ModalShell from './ModalShell';
import Button from './Button';

export default function ConfirmDialog({
  title,
  body,
  confirmLabel,
  cancelLabel,
  onConfirm,
  onCancel,
}) {
  return (
    <ModalShell
      role="alertdialog"
      label={title}
      onClose={onCancel}
      backdropClassName="fixed inset-0 z-50 flex bg-black/60 p-4"
      className="m-auto w-full max-w-sm rounded-xl border border-line bg-surface p-6 shadow-xl"
    >
      <h2 className="text-lg font-bold text-ink">{title}</h2>
      {body && <p className="mt-2 text-sm text-muted">{body}</p>}
      <div className="mt-6 flex justify-end gap-3">
        <Button variant="secondary" onClick={onCancel}>
          {cancelLabel}
        </Button>
        <Button variant="danger" onClick={onConfirm}>
          {confirmLabel}
        </Button>
      </div>
    </ModalShell>
  );
}
