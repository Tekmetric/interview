import { NavLink } from 'react-router';
import LogoIcon from '../Icons/LogoIcon.tsx';

const Header = () => {
  return (
    <div className="flex w-full border-b bg-white">
      <div className="mx-auto flex w-full max-w-[1280px] items-center gap-1 p-2">
        <LogoIcon className="flex h-[56px] shrink-0" />
        <nav className="px-5">
          <NavLink
            to={'/'}
            className={({ isActive }) =>
              isActive
                ? 'underline decoration-[2px] underline-offset-[3px]'
                : 'hover:underline hover:decoration-[2px] hover:underline-offset-[3px]'
            }
          >
            PRODUCTS
          </NavLink>
        </nav>
      </div>
    </div>
  );
};

export default Header;
