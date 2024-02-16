import React from 'react';
import { useAtom } from 'jotai';

import { lightTheme } from '../state/atoms';
import ReactComponent from '../interfaces/ReactChildrenProps';

const Footer: React.FC<ReactComponent> = () => {
    const [theme, _] = useAtom(lightTheme);

    return (
      <footer
        className={`flex justify-around items-center text-center h-10 ${theme ? 'text-white' : 'text-black'} ${theme ? 'bg-main' : 'bg-lightGrey'}`}
      >
        @Copyright Alin Oltean 2024
      </footer>
    );
};

export default Footer;