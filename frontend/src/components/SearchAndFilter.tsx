import {
  MagnifyingGlassIcon as SearchIcon,
  PlusIcon,
  XMarkIcon as CloseIcon,
} from '@heroicons/react/24/outline';
import React, { useCallback } from 'react';

import { FilterConfig } from '../types';

interface SearchAndFilterProps {
  filterConfig: FilterConfig;
  onFilterChange: (filters: Partial<FilterConfig>) => void;
  onAddNew: () => void;
}

export const SearchAndFilter: React.FC<SearchAndFilterProps> = React.memo(
  ({ filterConfig, onFilterChange, onAddNew }) => {
    const handleSearchChange = useCallback(
      (e: React.ChangeEvent<HTMLInputElement>) => {
        onFilterChange({ searchTerm: e.target.value });
      },
      [onFilterChange]
    );

    const handleStatusChange = useCallback(
      (e: React.ChangeEvent<HTMLSelectElement>) => {
        onFilterChange({ statusFilter: e.target.value });
      },
      [onFilterChange]
    );

    const handleClearFilters = useCallback(() => {
      onFilterChange({ searchTerm: '', statusFilter: '' });
    }, [onFilterChange]);

    const hasActiveFilters = filterConfig.searchTerm || filterConfig.statusFilter;

    return (
      <div className='mb-6 rounded-lg bg-white p-6 shadow transition-colors duration-200 dark:bg-gray-800 dark:shadow-gray-900/20'>
        <div className='flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center'>
          <div className='flex flex-1 flex-col gap-4 sm:flex-row'>
            {/* Search Input */}
            <div className='relative max-w-md flex-1'>
              <div className='pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3'>
                <SearchIcon className='size-5 text-gray-400' aria-hidden='true' />
              </div>
              <input
                type='text'
                placeholder='Search by name, email, or company...'
                value={filterConfig.searchTerm}
                onChange={handleSearchChange}
                className='form-input pl-10'
                aria-label='Search users by name, email, or company'
                role='searchbox'
              />
            </div>

            {/* Status Filter */}
            <div className='min-w-[150px]'>
              <label htmlFor='status-filter' className='sr-only'>
                Filter by status
              </label>
              <select
                id='status-filter'
                value={filterConfig.statusFilter}
                onChange={handleStatusChange}
                className='form-input'
                aria-label='Filter users by status'
              >
                <option value=''>All Status</option>
                <option value='Active'>Active</option>
                <option value='Inactive'>Inactive</option>
              </select>
            </div>

            {/* Clear Filters */}
            {hasActiveFilters && (
              <button
                onClick={handleClearFilters}
                className='btn-outline'
                aria-label='Clear all filters'
              >
                <CloseIcon className='mr-1 size-4' aria-hidden='true' />
                Clear
              </button>
            )}
          </div>

          {/* Add New Button */}
          <button onClick={onAddNew} className='btn-primary' aria-label='Add new user'>
            <PlusIcon className='mr-2 size-4' aria-hidden='true' />
            Add New
          </button>
        </div>
      </div>
    );
  }
);

SearchAndFilter.displayName = 'SearchAndFilter';
