import React from 'react';
import { useAtom } from 'jotai';
import { IconButton } from '@mui/material';
import WbSunnyIcon from '@mui/icons-material/WbSunny';
import ModeNightIcon from '@mui/icons-material/ModeNight';

import { lightTheme } from '../state/atoms';
import ReactComponent from '../interfaces/ReactChildrenProps';

const Header: React.FC<ReactComponent> = () => {
    const [theme, setTheme] = useAtom(lightTheme);

    return (
      <header className={`flex justify-around items-center h-10 ${theme ? 'bg-main' : 'bg-lightGrey'}`} >
        <img src="tekmetric.jpg" alt="logo" className="h-6" />
        <p>{theme}</p>
        <IconButton className="cursor-pointer" onClick={() => setTheme(!theme)}>
          {theme ? <WbSunnyIcon className='text-white'/> : <ModeNightIcon className='text-black' />}
        </IconButton>
      </header>
    );
};

export default Header;