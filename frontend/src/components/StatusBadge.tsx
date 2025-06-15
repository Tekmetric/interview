import React from 'react';
import { RepairServiceStatus, getStatusDisplayText } from '../types/enums';

type StatusBadgeProps = {
  status: RepairServiceStatus;
};

export const StatusBadge = ({ status }: StatusBadgeProps) => {
  let statusClass = '';

  switch (status) {
    case RepairServiceStatus.PENDING:
      statusClass = 'bg-yellow-100 text-yellow-800';
      break;
    case RepairServiceStatus.DIAGNOSED:
      statusClass = 'bg-purple-100 text-purple-800';
      break;
    case RepairServiceStatus.APPROVED:
      statusClass = 'bg-indigo-100 text-indigo-800';
      break;
    case RepairServiceStatus.IN_PROGRESS:
      statusClass = 'bg-blue-100 text-blue-800';
      break;
    case RepairServiceStatus.COMPLETED:
      statusClass = 'bg-green-100 text-green-800';
      break;
    case RepairServiceStatus.DELIVERED:
      statusClass = 'bg-teal-100 text-teal-800';
      break;
    case RepairServiceStatus.CANCELLED:
      statusClass = 'bg-red-100 text-red-800';
      break;
    default:
      statusClass = 'bg-gray-100 text-gray-800';
  }

  return (
    <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusClass}`}>
      {getStatusDisplayText(status)}
    </span>
  );
};
