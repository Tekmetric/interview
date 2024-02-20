import React from 'react';
import ReactDOM from 'react-dom';
import { useAtom } from 'jotai';
import { Box, IconButton } from '@mui/material';
import ClearIcon from '@mui/icons-material/Clear';

import { ModalI } from '../../interfaces/components';
import { lightTheme } from '../../state/atoms';

const Modal: React.FC<ModalI> = ({ open, children, onClose }) => {
  const [theme, _] = useAtom(lightTheme);

  if (!open) return null;

  return ReactDOM.createPortal(
    <>
      <Box className="modal-overlay fixed left-0 right-0 top-0 bottom-0 h-full bg-main bg-opacity-70 z-50" />

      <Box
        className={`modal-wrapper fixed m-auto left-0 right-0 top-0 bottom-0 h-96 w-96 z-50  ${theme ? 'bg-main' : 'bg-lightGrey'}`}
      >
        <IconButton onClick={onClose}>
          <ClearIcon
            className={`material-icon ${theme ? 'text-white' : 'text-black'} `}
          />
        </IconButton>
        {children}
      </Box>
    </>,
    document.getElementById('portal')!
  );
};

export default Modal;
