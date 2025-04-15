import React from 'react';

interface HeaderButtonProps {
  onClick: () => void;
  children: React.ReactNode;
}

export const HeaderButton = ({ onClick, children }: HeaderButtonProps) => {
  return (
    <button
      className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
      onClick={onClick}
    >
      {children}
    </button>
  );
};
