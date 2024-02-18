import React from 'react';

import { CardData } from '../../interfaces/components';

const ManufacturerCard: React.FC<CardData> = ({ cardData }) => {
  return (
    <>
      <div>{cardData.Mfr_CommonName}</div>
      <div>{cardData.Country}</div>
    </>
  );
};

export default ManufacturerCard;
