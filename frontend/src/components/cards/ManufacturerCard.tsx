import React, { useState } from 'react';
import { useAtom } from 'jotai';
import { Box, Container, IconButton, Typography } from '@mui/material';
import ZoomOutMapIcon from '@mui/icons-material/ZoomOutMap';

import Modal from '../modal/Modal';
import ModalView from '../modal/ModalView';
import { CardDataI } from '../../interfaces/components';
import { lightTheme } from '../../state/atoms';

const ManufacturerCard: React.FC<CardDataI> = ({ cardData }) => {
  const [theme, _] = useAtom(lightTheme);
  const [isOpen, setIsOpen] = useState(false);

  return (
    <Container>
      <Typography textOverflow="ellipsis" whiteSpace="nowrap" overflow="hidden">
        {cardData.Mfr_CommonName}
      </Typography>
      <Typography textOverflow="ellipsis" whiteSpace="nowrap" overflow="hidden">
        {cardData.Country}
      </Typography>

      <Box>
        <Modal open={isOpen} onClose={() => setIsOpen(false)}>
          <ModalView />
        </Modal>
        <IconButton onClick={() => setIsOpen(true)}>
          <ZoomOutMapIcon
            className={`material-icon absolute left-0 ${theme ? 'text-white' : 'text-black'}`}
          />
        </IconButton>
      </Box>
    </Container>
  );
};

export default ManufacturerCard;
