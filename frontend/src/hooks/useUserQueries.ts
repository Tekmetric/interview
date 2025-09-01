import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useCallback, useMemo, useState } from 'react';

import { ApiService } from '../services/ApiService';
import { ApiError, FilterConfig, PaginationInfo, SortConfig, TableData } from '../types';
import { UserFormData } from '../utils/validation';

// Toast function type
type ToastFunction = (title: string, message?: string) => void;

// Query keys for React Query
export const userKeys = {
  all: ['users'] as const,
  lists: () => [...userKeys.all, 'list'] as const,
  list: (filters: string) => [...userKeys.lists(), { filters }] as const,
  details: () => [...userKeys.all, 'detail'] as const,
  detail: (id: string) => [...userKeys.details(), id] as const,
};

// Hook for fetching all users with filtering, sorting, and pagination
export const useUsers = () => {
  const [filterConfig, setFilterConfig] = useState<FilterConfig>({
    searchTerm: '',
    statusFilter: '',
  });

  const [sortConfig, setSortConfig] = useState<SortConfig>({
    key: 'name',
    direction: 'asc',
  });

  const [pagination, setPagination] = useState<PaginationInfo>({
    currentPage: 1,
    totalPages: 1,
    pageSize: 10,
    totalRecords: 0,
  });

  const {
    data: allUsers = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: userKeys.lists(),
    queryFn: ApiService.fetchUsers,
    staleTime: 5 * 60 * 1000, // 5 minutes
    retry: 1,
  });

  // Client-side filtering and sorting
  const { filteredData, paginatedData } = useMemo(() => {
    let result = [...allUsers];

    // Apply search filter
    if (filterConfig.searchTerm) {
      const searchLower = filterConfig.searchTerm.toLowerCase();
      result = result.filter(
        item =>
          item.name.toLowerCase().includes(searchLower) ||
          item.email.toLowerCase().includes(searchLower) ||
          (item.company?.toLowerCase().includes(searchLower) ?? false)
      );
    }

    // Apply status filter
    if (filterConfig.statusFilter) {
      result = result.filter(item => item.status === filterConfig.statusFilter);
    }

    // Apply sorting
    result.sort((a, b) => {
      const aValue = a[sortConfig.key as keyof TableData] ?? '';
      const bValue = b[sortConfig.key as keyof TableData] ?? '';

      if (aValue < bValue) {
        return sortConfig.direction === 'asc' ? -1 : 1;
      }
      if (aValue > bValue) {
        return sortConfig.direction === 'asc' ? 1 : -1;
      }
      return 0;
    });

    // Calculate pagination
    const totalRecords = result.length;
    const totalPages = Math.ceil(totalRecords / pagination.pageSize);
    const currentPage = Math.min(pagination.currentPage, Math.max(1, totalPages));

    // Update pagination if needed
    if (totalPages !== pagination.totalPages || totalRecords !== pagination.totalRecords) {
      setPagination(prev => ({
        ...prev,
        totalRecords,
        totalPages,
        currentPage,
      }));
    }

    // Get current page data
    const startIndex = (currentPage - 1) * pagination.pageSize;
    const endIndex = startIndex + pagination.pageSize;
    const paginatedData = result.slice(startIndex, endIndex);

    return { filteredData: result, paginatedData };
  }, [
    allUsers,
    filterConfig,
    sortConfig,
    pagination.pageSize,
    pagination.currentPage,
    pagination.totalPages,
    pagination.totalRecords,
  ]);

  // Memoize callback functions to prevent unnecessary re-renders
  const updateFilters = useCallback((filters: Partial<FilterConfig>, page?: number) => {
    setFilterConfig(prev => ({ ...prev, ...filters }));
    setPagination(prev => ({ ...prev, currentPage: page || 1 }));
  }, []);

  // Handle sorting
  const handleSort = useCallback((key: string) => {
    setSortConfig(prev => ({
      key,
      direction: prev.key === key && prev.direction === 'asc' ? 'desc' : 'asc',
    }));
  }, []);

  // Navigate to page
  const goToPage = useCallback((page: number) => {
    setPagination(prev => ({
      ...prev,
      currentPage: Math.max(1, Math.min(page, prev.totalPages)),
    }));
  }, []);

  // Change page size
  const changePageSize = useCallback((pageSize: number) => {
    setPagination(prev => ({
      ...prev,
      pageSize,
      currentPage: 1,
    }));
  }, []);

  return {
    data: paginatedData,
    allData: allUsers,
    isLoading,
    error,
    refetch,
    pagination: {
      ...pagination,
      totalRecords: filteredData.length,
      totalPages: Math.ceil(filteredData.length / pagination.pageSize),
    },
    sortConfig,
    filterConfig,
    updateFilters,
    handleSort,
    goToPage,
    changePageSize,
  };
};

// Hook for fetching a single user
export const useUser = (id: string | undefined) => {
  return useQuery<TableData, ApiError>({
    queryKey: userKeys.detail(id || ''),
    queryFn: () => {
      if (!id) {
        // Create an ApiError for consistency
        const apiError: ApiError = {
          message: 'User ID is required. A valid user ID must be provided to fetch user details.',
          timestamp: new Date(),
        };
        throw apiError;
      }
      return ApiService.getUser(id);
    },
    enabled: !!id && id !== 'new',
    retry: 1,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

// Hook for creating a new user
export const useCreateUser = (onSuccess?: ToastFunction, onError?: ToastFunction) => {
  const queryClient = useQueryClient();

  return useMutation<TableData, ApiError, Omit<TableData, 'id' | 'createdAt'>>({
    mutationFn: (userData: Omit<TableData, 'id' | 'createdAt'>) => ApiService.createUser(userData),
    onSuccess: newUser => {
      // OPTIMIZED: Update the users list cache directly instead of invalidating
      queryClient.setQueryData(userKeys.lists(), (oldData: TableData[] = []) => {
        return [...oldData, newUser];
      });

      // Cache the individual user
      queryClient.setQueryData(userKeys.detail(newUser.id), newUser);

      // Show success toast
      if (onSuccess) {
        onSuccess('User Created', `${newUser.name} has been successfully created.`);
      }
    },
    onError: error => {
      console.error('Failed to create user:', error);

      // Show error toast
      if (onError) {
        onError(
          'Failed to Create User',
          error.message || 'An unexpected error occurred while creating the user.'
        );
      }
    },
  });
};

// Hook for updating an existing user
export const useUpdateUser = (
  userId: string,
  onSuccess?: ToastFunction,
  onError?: ToastFunction
) => {
  const queryClient = useQueryClient();

  return useMutation<TableData, ApiError, Partial<UserFormData>>({
    mutationFn: (userData: Partial<UserFormData>) => ApiService.updateUser(userId, userData),
    onSuccess: updatedUser => {
      // OPTIMIZED: Update the users list cache directly
      queryClient.setQueryData(userKeys.lists(), (oldData: TableData[] = []) => {
        return oldData.map(user => (user.id === userId ? updatedUser : user));
      });

      // Update the individual user cache
      queryClient.setQueryData(userKeys.detail(userId), updatedUser);

      // Show success toast
      if (onSuccess) {
        onSuccess('User Updated', `${updatedUser.name} has been successfully updated.`);
      }
    },
    onError: error => {
      console.error('Failed to update user:', error);

      // Show error toast
      if (onError) {
        onError(
          'Failed to Update User',
          error.message || 'An unexpected error occurred while updating the user.'
        );
      }
    },
  });
};

// Hook for deleting a user
export const useDeleteUser = (onSuccess?: ToastFunction, onError?: ToastFunction) => {
  const queryClient = useQueryClient();

  return useMutation<boolean, ApiError, string>({
    mutationFn: (userId: string) => ApiService.deleteUser(userId),
    onSuccess: (_, deletedUserId) => {
      // OPTIMIZED: Remove from users list cache directly
      queryClient.setQueryData(userKeys.lists(), (oldData: TableData[] = []) => {
        return oldData.filter(user => user.id !== deletedUserId);
      });

      // Remove the individual user from cache
      queryClient.removeQueries({ queryKey: userKeys.detail(deletedUserId) });

      // Show success toast
      if (onSuccess) {
        onSuccess('User Deleted', 'The user has been successfully deleted.');
      }
    },
    onError: error => {
      console.error('Failed to delete user:', error);

      // Show error toast
      if (onError) {
        onError(
          'Failed to Delete User',
          error.message || 'An unexpected error occurred while deleting the user.'
        );
      }
    },
  });
};

// Custom hook that combines validation with mutation
export const useUserForm = (
  userId?: string,
  isNewUser = false,
  onSuccess?: ToastFunction,
  onError?: ToastFunction
) => {
  const createMutation = useCreateUser(onSuccess, onError);
  const updateMutation = useUpdateUser(userId || '', onSuccess, onError);

  const submitForm = async (formData: UserFormData) => {
    if (isNewUser) {
      return createMutation.mutateAsync({
        name: formData.name,
        email: formData.email,
        phone: formData.phone || '',
        company: formData.company || '',
        status: formData.status,
      });
    } else if (userId) {
      return updateMutation.mutateAsync(formData);
    }
    throw new Error('Invalid form submission state');
  };

  return {
    submitForm,
    isLoading: createMutation.isPending || updateMutation.isPending,
    error: createMutation.error || updateMutation.error,
    isSuccess: createMutation.isSuccess || updateMutation.isSuccess,
    reset: () => {
      createMutation.reset();
      updateMutation.reset();
    },
  };
};
