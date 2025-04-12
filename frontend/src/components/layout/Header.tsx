import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { MenuIcon, UserIcon, ChevronDownIcon } from '../svg';

interface HeaderProps {
  toggleNavbar: () => void;
}

export const Header = ({ toggleNavbar }: HeaderProps) => {
  const { logout, user } = useAuth0();
  const [userMenuOpen, setUserMenuOpen] = React.useState(false);

  const handleLogout = () => {
    logout({ logoutParams: { returnTo: window.location.origin } });
  };

  const handleUserMenuToggle = () => {
    setUserMenuOpen(!userMenuOpen);
  };

  return (
    <header className="bg-white shadow-md h-16 flex items-center justify-between px-4">
      <button
        onClick={toggleNavbar}
        className="p-2 rounded-md hover:bg-gray-100 focus:outline-none"
        aria-label="Toggle navigation menu"
      >
        <MenuIcon />
      </button>

      <div className="relative">
        <button
          onClick={handleUserMenuToggle}
          className="flex items-center space-x-2 p-2 rounded-md hover:bg-gray-100 focus:outline-none"
          aria-label="User menu"
        >
          <div className="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center overflow-hidden">
            {user?.picture ? (
              <img src={user.picture} alt="User avatar" className="w-full h-full object-cover" />
            ) : (
              <UserIcon />
            )}
          </div>
          <ChevronDownIcon />
        </button>

        {userMenuOpen && (
          <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-10">
            <button
              className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              onClick={() => {
                setUserMenuOpen(false);
              }}
            >
              User details
            </button>
            <button
              className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              onClick={() => {
                setUserMenuOpen(false);
                handleLogout();
              }}
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
};
