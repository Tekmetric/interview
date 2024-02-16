import React from 'react';
import ReactComponent from '../interfaces/ReactChildrenProps';

const Footer: React.FC<ReactComponent> = () => {
    return (
      <footer className="flex justify-around items-center text-center bg-main h-10 text-white">
        @Copyright Alin Oltean 2024
      </footer>
    );
};

export default Footer;