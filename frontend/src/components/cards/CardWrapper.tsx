import React from 'react';
import { useAtom } from 'jotai';
import { Box, Card } from '@mui/material';

import { ReactComponentI } from '../../interfaces/components';
import { lightTheme } from '../../state/atoms';

const CardWrapper: React.FC<ReactComponentI> = ({ children }) => {
  const [theme, _] = useAtom(lightTheme);

  return (
    <>
      <Card className="cursor-pointer rounded-sm hover:scale-110 hover:ease-in-out duration-150">
        <Box
          className={`flex flex-col card-data p-4 h-20 justify-center ${theme ? 'text-white' : 'text-black'} ${theme ? 'bg-main' : 'bg-lightGrey'}`}
        >
          {children}
        </Box>
      </Card>
    </>
  );
};

export default CardWrapper;
