import React from 'react';
import { useAtom } from 'jotai';
import { Card } from '@mui/material';

import { ReactComponent } from '../../interfaces/components';
import { lightTheme } from '../../state/atoms';

const CardWrapper: React.FC<ReactComponent> = ({ children }) => {
  const [theme, _] = useAtom(lightTheme);

  return (
    <Card className="cursor-pointer hover:scale-110 hover:ease-in-out duration-150">
      <section
        className={`flex flex-col card-data p-2 h-20 ${theme ? 'text-white' : 'text-black'} ${theme ? 'bg-main' : 'bg-lightGrey'}`}
      >
        {children}
      </section>
    </Card>
  );
};

export default CardWrapper;
