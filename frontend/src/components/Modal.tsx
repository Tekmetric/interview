import { CloseIcon } from './svg';

type ModalProps = {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
};

export const Modal = ({ isOpen, onClose, title, children, footer }: ModalProps) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto">
      <div className="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center block p-0">
        <div className="fixed inset-0 transition-opacity" aria-hidden="true" onClick={onClose}>
          <div className="absolute inset-0 bg-gray-500 opacity-75"></div>
        </div>

        <div
          className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all my-8 align-middle max-w-lg w-full"
          role="dialog"
          aria-modal="true"
          aria-labelledby="modal-headline"
        >
          <div className="bg-gray-50 px-4 py-3 border-b border-gray-200 px-6 flex justify-between items-center">
            <h3 className="text-lg leading-6 font-medium text-gray-900" id="modal-headline">
              {title}
            </h3>
            <button
              type="button"
              className="bg-white rounded-md text-gray-400 hover:text-gray-500 focus:outline-none"
              onClick={onClose}
            >
              <span className="sr-only">Close</span>
              <CloseIcon />
            </button>
          </div>

          <div className="bg-white px-4 pt-5 pb-4 p-6 pb-4">{children}</div>

          {footer && (
            <div className="bg-gray-50 px-4 py-3 px-6 flex flex-row-reverse">{footer}</div>
          )}
        </div>
      </div>
    </div>
  );
};
