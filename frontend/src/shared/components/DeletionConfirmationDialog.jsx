import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import PropTypes from 'prop-types';

export default function DeletionConfirmationDialog({ open, onConfirm, onCancel }) {
  return (
    <Dialog
      sx={{ '& .MuiDialog-paper': { width: '80%', maxHeight: 435 } }}
      maxWidth="xs"
      open={open}>
      <DialogTitle>Confirmation</DialogTitle>
      <DialogContent dividers>Are you sure you want to delete this car?</DialogContent>
      <DialogActions>
        <Button autoFocus onClick={onCancel}>
          Cancel
        </Button>
        <Button onClick={onConfirm}>Ok</Button>
      </DialogActions>
    </Dialog>
  );
}

DeletionConfirmationDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onConfirm: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired
};
