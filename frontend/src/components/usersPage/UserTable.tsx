import {
  Bars3Icon as SortIcon,
  BarsArrowDownIcon as SortDescIcon,
  BarsArrowUpIcon as SortAscIcon,
} from '@heroicons/react/24/outline';
import React from 'react';
import { twMerge } from 'tailwind-merge';

import { useIsDesktop } from '../../hooks/useMediaQuery';
import { SortConfig, TableData } from '../../types';

interface UserTableProps {
  data: TableData[];
  sortConfig: SortConfig;
  onSort: (key: string) => void;
  onEdit: (record: TableData) => void;
  onDelete: (id: string) => void;
  onView: (record: TableData) => void;
}

export const UserTable: React.FC<UserTableProps> = ({
  data,
  sortConfig,
  onSort,
  onEdit,
  onDelete,
  onView,
}) => {
  const isDesktop = useIsDesktop();

  // Helper functions for generating dynamic classes
  const getStatusClasses = (status: string) => {
    const baseClasses = 'inline-flex rounded-full px-2 py-1 text-xs font-semibold';
    return status === 'Active'
      ? `${baseClasses} bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300`
      : `${baseClasses} bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300`;
  };

  const getActionButtonClasses = (color: 'blue' | 'indigo' | 'red') => {
    const colorVariants = {
      blue: 'text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300',
      indigo:
        'text-indigo-600 hover:text-indigo-900 dark:text-indigo-400 dark:hover:text-indigo-300',
      red: 'text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300',
    };
    return `transition-colors duration-200 ${colorVariants[color]}`;
  };

  const getSortIcon = (columnKey: string) => {
    if (sortConfig.key !== columnKey) {
      return <SortIcon className='ml-1 size-4' aria-hidden='true' />;
    }

    return sortConfig.direction === 'asc' ? (
      <SortAscIcon className='ml-1 size-4' aria-hidden='true' />
    ) : (
      <SortDescIcon className='ml-1 size-4' aria-hidden='true' />
    );
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  // Render desktop view
  if (isDesktop) {
    return (
      <div className='table-container'>
        <table className='table-desktop' role='table' aria-label='User data table'>
          <thead className='table-head'>
            <tr>
              <th
                className='table-th-sortable'
                onClick={() => onSort('name')}
                role='columnheader'
                aria-sort={
                  sortConfig.key === 'name'
                    ? sortConfig.direction === 'asc'
                      ? 'ascending'
                      : 'descending'
                    : 'none'
                }
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && onSort('name')}
                aria-label='Sort by name'
              >
                <div className='flex items-center'>
                  Name
                  {getSortIcon('name')}
                </div>
              </th>
              <th
                className='table-th-sortable'
                onClick={() => onSort('email')}
                role='columnheader'
                aria-sort={
                  sortConfig.key === 'email'
                    ? sortConfig.direction === 'asc'
                      ? 'ascending'
                      : 'descending'
                    : 'none'
                }
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && onSort('email')}
                aria-label='Sort by email'
              >
                <div className='flex items-center'>
                  Email
                  {getSortIcon('email')}
                </div>
              </th>
              <th
                className='table-th-sortable'
                onClick={() => onSort('status')}
                role='columnheader'
                aria-sort={
                  sortConfig.key === 'status'
                    ? sortConfig.direction === 'asc'
                      ? 'ascending'
                      : 'descending'
                    : 'none'
                }
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && onSort('status')}
                aria-label='Sort by status'
              >
                <div className='flex items-center'>
                  Status
                  {getSortIcon('status')}
                </div>
              </th>
              <th
                className='table-th-sortable hidden xl:table-cell'
                onClick={() => onSort('company')}
                role='columnheader'
                aria-sort={
                  sortConfig.key === 'company'
                    ? sortConfig.direction === 'asc'
                      ? 'ascending'
                      : 'descending'
                    : 'none'
                }
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && onSort('company')}
                aria-label='Sort by company'
              >
                <div className='flex items-center'>
                  Company
                  {getSortIcon('company')}
                </div>
              </th>
              <th
                className='table-th-sortable hidden 2xl:table-cell'
                onClick={() => onSort('createdAt')}
                role='columnheader'
                aria-sort={
                  sortConfig.key === 'createdAt'
                    ? sortConfig.direction === 'asc'
                      ? 'ascending'
                      : 'descending'
                    : 'none'
                }
                tabIndex={0}
                onKeyDown={e => e.key === 'Enter' && onSort('createdAt')}
                aria-label='Sort by creation date'
              >
                <div className='flex items-center'>
                  Created
                  {getSortIcon('createdAt')}
                </div>
              </th>
              <th className='table-th' role='columnheader'>
                Actions
              </th>
            </tr>
          </thead>
          <tbody className='table-tbody'>
            {data.map(record => (
              <tr key={record.id} className='table-row'>
                <td className='table-td'>
                  <div className='flex items-center'>
                    <div className='size-8 shrink-0 xl:size-10'>
                      <div
                        className='flex size-full items-center justify-center rounded-full bg-gradient-to-r from-purple-400 to-pink-400 text-xs font-medium text-white xl:text-sm'
                        role='img'
                        aria-label={`Avatar for ${record.name}`}
                      >
                        {record.name.charAt(0)}
                      </div>
                    </div>
                    <div className='ml-3 xl:ml-4'>
                      <div className='text-sm font-medium text-gray-900 dark:text-white'>
                        {record.name}
                      </div>
                      <div className='text-xs text-gray-500 dark:text-gray-400 xl:text-sm'>
                        {record.phone}
                      </div>
                    </div>
                  </div>
                </td>
                <td className='table-td'>
                  <div className='text-sm text-gray-900 dark:text-white'>{record.email}</div>
                </td>
                <td className='table-td'>
                  <span className={getStatusClasses(record.status)}>{record.status}</span>
                </td>
                <td className='table-td hidden xl:table-cell'>{record.company}</td>
                <td className='table-td hidden 2xl:table-cell text-gray-500 dark:text-gray-400'>
                  {formatDate(record.createdAt)}
                </td>
                <td className='table-td'>
                  <div className='table-actions'>
                    <button
                      onClick={() => onView(record)}
                      className={getActionButtonClasses('blue')}
                      aria-label={`View details for ${record.name}`}
                    >
                      View
                    </button>
                    <button
                      onClick={() => onEdit(record)}
                      className={getActionButtonClasses('indigo')}
                      aria-label={`Edit ${record.name}`}
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => onDelete(record.id)}
                      className={getActionButtonClasses('red')}
                      aria-label={`Delete ${record.name}`}
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {data.length === 0 && (
          <div className='table-empty'>
            <div className='table-empty-text text-lg'>No data found</div>
            <div className='mt-2 text-sm text-gray-400 dark:text-gray-500'>
              Try adjusting your search or filters
            </div>
          </div>
        )}
      </div>
    );
  }

  // Render mobile view
  return (
    <div className='space-y-4'>
      {data.map(record => (
        <div key={record.id} className='table-mobile-card'>
          <div className='flex items-start justify-between'>
            <div className='flex items-center'>
              <div className='size-10 shrink-0'>
                <div
                  className='flex size-full items-center justify-center rounded-full bg-gradient-to-r from-purple-400 to-pink-400 text-sm font-medium text-white'
                  role='img'
                  aria-label={`Avatar for ${record.name}`}
                >
                  {record.name.charAt(0)}
                </div>
              </div>
              <div className='ml-3'>
                <div className='text-sm font-medium text-gray-900 dark:text-white'>
                  {record.name}
                </div>
                <div className='text-xs text-gray-500 dark:text-gray-400'>{record.phone}</div>
              </div>
            </div>
            <span className={getStatusClasses(record.status)}>{record.status}</span>
          </div>

          <div className='mt-4 space-y-2'>
            <div className='table-mobile-field'>
              <span className='table-mobile-label'>Email:</span>
              <span className='table-mobile-value'>{record.email}</span>
            </div>
            <div className='table-mobile-field'>
              <span className='table-mobile-label'>Company:</span>
              <span className='table-mobile-value'>{record.company}</span>
            </div>
            <div className='table-mobile-field'>
              <span className='table-mobile-label'>Created:</span>
              <span className='table-mobile-value text-gray-500 dark:text-gray-400'>
                {formatDate(record.createdAt)}
              </span>
            </div>
          </div>

          <div className='mt-4 flex justify-end space-x-3'>
            <button
              onClick={() => onView(record)}
              className={twMerge('text-xs', getActionButtonClasses('blue'))}
              aria-label={`View details for ${record.name}`}
            >
              View
            </button>
            <button
              onClick={() => onEdit(record)}
              className={twMerge('text-xs', getActionButtonClasses('indigo'))}
              aria-label={`Edit ${record.name}`}
            >
              Edit
            </button>
            <button
              onClick={() => onDelete(record.id)}
              className={twMerge('text-xs', getActionButtonClasses('red'))}
              aria-label={`Delete ${record.name}`}
            >
              Delete
            </button>
          </div>
        </div>
      ))}

      {data.length === 0 && (
        <div className='table-mobile-card table-empty'>
          <div className='table-empty-text text-base'>No data found</div>
          <div className='mt-2 text-xs text-gray-400 dark:text-gray-500'>
            Try adjusting your search or filters
          </div>
        </div>
      )}
    </div>
  );
};
