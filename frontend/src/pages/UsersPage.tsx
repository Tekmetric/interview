import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import {
  ConfirmationModal,
  LoadingSpinner,
  Pagination,
  SearchAndFilter,
  UserTable,
} from '../components';
import { useToastContext } from '../contexts/ToastContext';
import { useDeleteUser, useUsers } from '../hooks/useUserQueries';
import { TableData } from '../types';

export const UsersPage: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { addSuccessToast, addErrorToast } = useToastContext();

  // Confirmation modal state
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState<TableData | null>(null);

  // Track if we've initialized from URL params
  const [hasInitialized, setHasInitialized] = useState(false);

  // Extract URL parameters using useMemo to avoid unnecessary recalculations
  const urlParams = useMemo(
    () => ({
      page: searchParams.get('page'),
      search: searchParams.get('search'),
      status: searchParams.get('status'),
    }),
    [searchParams]
  );

  // Use React Query hooks
  const {
    data,
    isLoading,
    error,
    pagination,
    sortConfig,
    filterConfig,
    updateFilters,
    handleSort,
    goToPage,
    changePageSize,
    refetch,
  } = useUsers();

  const deleteUserMutation = useDeleteUser(addSuccessToast, addErrorToast);

  // Initialize from URL params only once on mount
  useEffect(() => {
    if (!hasInitialized) {
      // Initialize filters and pagination from URL in one call
      const pageNum = urlParams.page ? parseInt(urlParams.page) : 1;
      updateFilters(
        {
          searchTerm: urlParams.search || '',
          statusFilter: urlParams.status || '',
        },
        pageNum
      );

      setHasInitialized(true);
    }
  }, [hasInitialized, urlParams.page, urlParams.search, urlParams.status, updateFilters]);

  // Update URL when filters or pagination change
  useEffect(() => {
    if (hasInitialized) {
      const params = new URLSearchParams();

      // Always include page parameter if it was originally in the URL or if page > 1
      if (pagination.currentPage > 1 || urlParams.page) {
        params.set('page', pagination.currentPage.toString());
      }

      if (filterConfig.searchTerm) params.set('search', filterConfig.searchTerm);
      if (filterConfig.statusFilter) params.set('status', filterConfig.statusFilter);

      setSearchParams(params, { replace: true });
    }
  }, [
    hasInitialized,
    pagination.currentPage,
    filterConfig.searchTerm,
    filterConfig.statusFilter,
    setSearchParams,
    urlParams.page,
  ]);

  const handleAddNew = () => {
    navigate('/users/new', {
      state: {
        returnTo: `/users?${searchParams.toString()}`,
      },
    });
  };

  const handleEdit = (record: TableData) => {
    // Navigate to edit with state
    navigate(`/users/${record.id}/edit`, {
      state: {
        record,
        returnTo: `/users?${searchParams.toString()}`,
      },
    });
  };

  const handleView = (record: TableData) => {
    // Navigate to view mode (editMode defaults to false)
    navigate(`/users/${record.id}`, {
      state: {
        record,
        returnTo: `/users?${searchParams.toString()}`,
      },
    });
  };

  const handleDeleteClick = (id: string) => {
    // Find the user to delete
    const user = data.find((u: TableData) => u.id === id);
    if (user) {
      setUserToDelete(user);
      setShowConfirmModal(true);
    }
  };

  const handleConfirmDelete = async () => {
    if (userToDelete) {
      try {
        await deleteUserMutation.mutateAsync(userToDelete.id);
        setShowConfirmModal(false);
        setUserToDelete(null);
      } catch (error) {
        console.error('Failed to delete user:', error);
      }
    }
  };

  const handleCancelDelete = () => {
    setShowConfirmModal(false);
    setUserToDelete(null);
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <div className='py-12 text-center'>
        <h2 className='mb-4 text-2xl font-bold status-error'>Error Loading Users</h2>
        <p className='mb-4 text-gray-600 dark:text-gray-400'>{error.message}</p>
        <button onClick={() => refetch()} className='btn-primary'>
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className='space-y-6'>
      {/* Search and Filter */}
      <SearchAndFilter
        filterConfig={filterConfig}
        onFilterChange={updateFilters}
        onAddNew={handleAddNew}
      />

      {/* Users Table */}
      <UserTable
        data={data}
        sortConfig={sortConfig}
        onSort={handleSort}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDeleteClick}
      />

      {/* Pagination */}
      <Pagination
        pagination={pagination}
        onPageChange={goToPage}
        onPageSizeChange={changePageSize}
      />

      {/* Delete Confirmation Modal */}
      <ConfirmationModal
        isOpen={showConfirmModal}
        onClose={handleCancelDelete}
        onConfirm={handleConfirmDelete}
        title='Delete User'
        message='Are you sure you want to delete this user? This action cannot be undone.'
        user={userToDelete}
        confirmText='Delete'
        cancelText='Cancel'
        isDestructive={true}
      />
    </div>
  );
};
