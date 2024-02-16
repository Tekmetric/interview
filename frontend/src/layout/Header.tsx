import React from 'react';
import ReactComponent from '../interfaces/ReactChildrenProps';

const Header: React.FC<ReactComponent> = () => {
    return (
      <header className="flex justify-around items-center bg-main h-10">
        <img src="tekmetric.jpg" alt="logo" className="h-6" />
      </header>
    );
};

export default Header;