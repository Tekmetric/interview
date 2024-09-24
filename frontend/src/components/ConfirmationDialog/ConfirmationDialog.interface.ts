export interface IConfirmationDialogProps {
  open: boolean;
  message: string;
  onConfirm: () => void;
  onDiscard: () => void;
  title?: string;
}
