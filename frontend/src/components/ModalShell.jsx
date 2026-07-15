import { useRef } from 'react';
import { useFocusTrap } from '../hooks/useFocusTrap';
import { useBodyScrollLock } from '../hooks/useBodyScrollLock';

// Shared overlay chrome for dialogs: backdrop, click-outside-to-close, focus
// trap, Escape handling, and body scroll lock — so each dialog only supplies its
// own content and the accessibility is correct in one place.
export default function ModalShell({
  onClose,
  label,
  role = 'dialog',
  className = 'm-auto w-full max-w-2xl rounded-xl border border-line bg-surface p-5 shadow-xl',
  backdropClassName = 'fixed inset-0 z-50 flex overflow-y-auto bg-black/60 p-4 sm:p-8',
  children,
}) {
  const ref = useRef(null);
  useFocusTrap(ref, { onEscape: onClose });
  useBodyScrollLock();

  return (
    <div className={backdropClassName} onClick={onClose}>
      <div
        ref={ref}
        role={role}
        aria-modal="true"
        aria-label={label}
        tabIndex={-1}
        onClick={(e) => e.stopPropagation()}
        className={`${className} focus:outline-none`}
      >
        {children}
      </div>
    </div>
  );
}
