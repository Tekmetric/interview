import React from 'react';
import { useAtom } from 'jotai';
import { Card } from '@mui/material';

import { CardData } from '../interfaces/components';
import { lightTheme } from '../state/atoms';

const CardItem: React.FC<CardData> = ({ cardData }) => {
  const [theme, _] = useAtom(lightTheme);

  return (
    <Card className="cursor-pointer hover:scale-110 hover:ease-in-out duration-150">
      <section
        className={`flex flex-col card-data p-2 h-20 ${theme ? 'text-white' : 'text-black'} ${theme ? 'bg-main' : 'bg-lightGrey'}`}
      >
        <div>{cardData.Mfr_CommonName}</div>
        <div>{cardData.Country}</div>
      </section>
    </Card>
  );
};

export default CardItem;
