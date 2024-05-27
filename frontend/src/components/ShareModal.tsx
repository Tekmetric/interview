import { Button, Modal, Typography } from '@mui/material';
import { useShareDialog } from '../hooks';
import { buildImdbURL } from '../utils/url';

const style = {
  transform: 'translate(-50%, -50%)',
};

const ShareModal = () => {
  const { activeImdbID, close } = useShareDialog();
  return (
    <Modal open={!!activeImdbID} onClose={close}>
      <div
        className="flex flex-col gap-y-2 absolute top-1/2 left-1/2 w-96 bg-white p-4 rounded-lg border border-cyan-200"
        style={style}
      >
        <Typography id="modal-modal-title" variant="h6" component="h2">
          Share the movie
        </Typography>
        <Typography id="modal-modal-description">
          Copy the link to share the IMDb page of the movie!
        </Typography>
        <Button
          color="secondary"
          onClick={() => {
            navigator.clipboard.writeText(buildImdbURL(activeImdbID ?? ''));
            close();
          }}
        >
          Copy link
        </Button>
      </div>
    </Modal>
  );
};

export default ShareModal;
