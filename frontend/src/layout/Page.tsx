import React from 'react';
import { Outlet } from 'react-router-dom';
import { Menu } from '../components';

export const Page: React.FC = () => {
  return (
    <>
      <Menu />
      <div className="max-w-[1280px] ps-m pe-m m-i-[auto]">
        <Outlet />
      </div>
    </>
  );
};
