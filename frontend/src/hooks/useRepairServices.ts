import useSWR from 'swr';
import { fetchRepairServices } from '../services/api';
import { ApiResponse, PageableResponse, RepairService } from '../types/api';

export function useRepairServices(
  pageNumber = 0,
  pageSize = 10,
  sortBy?: string,
  sortDirection?: 'asc' | 'desc'
) {
  const { data, error, isLoading, mutate } = useSWR<
    ApiResponse<PageableResponse<RepairService>>,
    Error
  >(
    [`repair-services`, pageNumber, pageSize, sortBy, sortDirection],
    () => fetchRepairServices(pageNumber, pageSize, sortBy, sortDirection),
    {
      dedupingInterval: 500,
    }
  );

  return {
    data: data?.data?.content || [],
    pageInfo: data?.data
      ? {
          currentPage: data.data.pageable.pageNumber,
          totalPages: data.data.totalPages,
          totalItems: data.data.totalElements,
          pageSize: data.data.pageable.pageSize,
        }
      : null,
    isLoading,
    error,
    mutate,
  };
}
