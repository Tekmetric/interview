import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import { useNavigate } from 'react-router-dom';
import { MenuIcon, UserIcon, ChevronDownIcon } from '../svg';
import { HeaderButton } from './HeaderButton';

interface HeaderProps {
  toggleNavbar: () => void;
}

export const Header = ({ toggleNavbar }: HeaderProps) => {
  const { logout, user } = useAuth0();
  const [userMenuOpen, setUserMenuOpen] = React.useState(false);
  const navigate = useNavigate();

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
            <HeaderButton
              onClick={() => {
                setUserMenuOpen(false);
                navigate('/user');
              }}
            >
              User details
            </HeaderButton>
            <HeaderButton
              onClick={() => {
                setUserMenuOpen(false);
                handleLogout();
              }}
            >
              Logout
            </HeaderButton>
          </div>
        )}
      </div>
    </header>
  );
};
