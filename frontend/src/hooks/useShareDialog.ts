import { useContext } from 'react';
import { ShareDialogContext } from '../state/ShareDialogContext';

const useShareDialog = () => {
  const context = useContext(ShareDialogContext);
  if (!context) {
    throw new Error('Share context must be not null');
  }

  return context;
};

export default useShareDialog;
