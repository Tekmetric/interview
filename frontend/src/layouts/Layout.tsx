import { Outlet } from 'react-router';
import Header from '../components/Header/Header.tsx';

const Layout = () => {
  return (
    <div className="flex h-full w-full flex-grow flex-col bg-[#f9f7f5]">
      <Header />
      <div className="mx-auto flex h-full w-full max-w-[1280px] flex-grow flex-col p-4">
        <Outlet />
      </div>
    </div>
  );
};

export default Layout;
