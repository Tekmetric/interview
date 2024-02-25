import React, { useState } from 'react';
import { Box, Container, IconButton, Typography } from '@mui/material';

import Modal from '../modal/Modal';
import ModalView from '../modal/ModalView';
import { CardDataI } from '../../interfaces/components';

const ManufacturerCard: React.FC<CardDataI> = ({ cardData }) => {
  const [isOpen, setIsOpen] = useState(false);


  return (
    <>
      <Container onClick={() => setIsOpen(true)}>
        <Typography
          textOverflow="ellipsis"
          whiteSpace="nowrap"
          overflow="hidden"
        >
          {cardData.Mfr_CommonName}
        </Typography>
        <Typography
        >
          {cardData.Country}
        </Typography>
      </Container>
      <Box>
        <Modal open={isOpen} onClose={() => setIsOpen(false)}>
          <ModalView />
        </Modal>
      </Box>
    </>
  );
};

export default ManufacturerCard;
