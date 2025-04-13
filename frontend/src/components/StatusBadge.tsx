import React from 'react';

type StatusBadgeProps = {
  status: string;
};

export const StatusBadge = ({ status }: StatusBadgeProps) => {
  let statusClass = '';

  switch (status) {
    case 'PENDING':
      statusClass = 'bg-yellow-100 text-yellow-800';
      break;
    case 'IN_PROGRESS':
      statusClass = 'bg-blue-100 text-blue-800';
      break;
    case 'COMPLETED':
      statusClass = 'bg-green-100 text-green-800';
      break;
    case 'CANCELLED':
      statusClass = 'bg-red-100 text-red-800';
      break;
    default:
      statusClass = 'bg-gray-100 text-gray-800';
  }

  return (
    <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusClass}`}>
      {status.replace('_', ' ')}
    </span>
  );
};
