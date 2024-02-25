import React from 'react';
import { Grid } from '@mui/material';
import { useAtom } from 'jotai';

import { ReactComponentI } from '../../interfaces/components';
import { lightTheme } from '../../state/atoms';

const ModalView: React.FC<ReactComponentI> = () => {
  const [theme, _] = useAtom(lightTheme);
  return (
    <Grid
      spacing={6}
      container
      className="items-center justify-center p-6 h-full"
      alignContent="center"
    >
      <Grid item>
        <img src="tesla.png" alt="Tesla logo" className="h-20" />
      </Grid>
      <Grid item className={`${theme ? 'text-white' : 'text-black'}`}>
        Tesla, Inc. (/ˈtɛslə/ TESS-lə or /ˈtɛzlə/ TEZ-lə[a]) is an American
        multinational automotive and clean energy company headquartered in
        Austin, Texas, which designs, manufactures and sells electric vehicles,
        stationary battery energy storage devices from home to grid-scale, solar
        panels and solar shingles, and related products and services.
      </Grid>
    </Grid>
  );
};

export default ModalView;
