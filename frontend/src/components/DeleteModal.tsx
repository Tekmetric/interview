import React from "react";
import { Modal, Box, Typography, Button } from "@mui/material";
import styled from "@emotion/styled";

interface DeleteModalProps {
  open: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

const ModalBox = styled(Box)`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
  background-color: white;
  border: 2px solid #000;
  box-shadow: 24px;
  padding: 16px 32px 24px;
  border-radius: 8px;
`;

const ButtonsContainer = styled.div`
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
`;

function DeleteModal({ open, onClose, onConfirm }: DeleteModalProps) {
  return (
    <Modal open={open} onClose={onClose}>
      <ModalBox>
        <Typography variant="h6" component="h2">
          Confirm Deletion
        </Typography>
        <Typography variant="body1" sx={{ mt: 2 }}>
          Are you sure you want to delete this event? This action cannot be
          undone.
        </Typography>
        <ButtonsContainer>
          <Button
            onClick={onClose}
            variant="outlined"
            color="primary"
            sx={{ mr: 2 }}
          >
            Cancel
          </Button>
          <Button onClick={onConfirm} variant="contained" color="error">
            Delete
          </Button>
        </ButtonsContainer>
      </ModalBox>
    </Modal>
  );
}

export default DeleteModal;
