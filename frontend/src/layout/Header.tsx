import React from 'react';
import { useAtom } from 'jotai';
import IconButton from '@mui/material/IconButton';
import WbSunnyIcon from '@mui/icons-material/WbSunny';
import ModeNightIcon from '@mui/icons-material/ModeNight';

import { lightTheme } from '../state/atoms';
import { ReactComponent } from '../interfaces/components';

const Header: React.FC<ReactComponent> = () => {
    const [theme, setTheme] = useAtom(lightTheme);

    return (
      <header className={`flex justify-around items-center ${theme ? 'bg-main' : 'bg-lightGrey'}`} >
        <img src="tekmetric.jpg" alt="logo" className="h-8" />
        <p>{theme}</p>
        <IconButton className="cursor-pointer hover:scale-110" onClick={() => setTheme(!theme)}>
          {theme ? <WbSunnyIcon className='text-white'/> : <ModeNightIcon className='text-black' />}
        </IconButton>
      </header>
    );
};

export default Header;