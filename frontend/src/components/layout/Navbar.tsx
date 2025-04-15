import React from 'react';
import { Link } from 'react-router-dom';
import { RepairServiceIcon } from '../svg';

interface NavbarProps {
  isOpen: boolean;
}

export const Navbar = ({ isOpen }: NavbarProps) => {
  return (
    <nav
      className={`bg-gray-800 text-white w-64 fixed h-full transition-all duration-300 ease-in-out transform ${
        isOpen ? 'translate-x-0' : '-translate-x-full'
      } z-10`}
    >
      <div className="p-4">
        <h2 className="text-xl font-bold mb-6 pb-2 border-b border-gray-700">Car Repair Shop</h2>
        <ul className="space-y-2">
          <li>
            <Link
              to="/"
              className="flex items-center px-4 py-2 text-gray-300 hover:bg-gray-700 hover:text-white rounded-md"
            >
              <RepairServiceIcon className="h-5 w-5 mr-3" />
              Repair Service
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};
