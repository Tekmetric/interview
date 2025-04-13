import React from 'react';

type PaginationButtonProps = {
  onClick: () => void;
  disabled: boolean;
  children: React.ReactNode;
};

export const PaginationButton = ({ onClick, disabled, children }: PaginationButtonProps) => {
  return (
    <button
      className="w-24 px-3 py-1 border border-gray-300 bg-white text-sm font-medium rounded-md text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
};
