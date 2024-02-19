import React from 'react';

import { CardData } from '../../interfaces/components';
import { Typography } from '@mui/material';

const ManufacturerCard: React.FC<CardData> = ({ cardData }) => {
  return (
    <>
      <Typography textOverflow="ellipsis" whiteSpace="nowrap" overflow="hidden">
        {cardData.Mfr_CommonName}
      </Typography>
      <Typography textOverflow="ellipsis" whiteSpace="nowrap" overflow="hidden">
        {cardData.Country}
      </Typography>
    </>
  );
};

export default ManufacturerCard;
