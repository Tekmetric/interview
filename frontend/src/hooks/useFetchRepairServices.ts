import useSWR from 'swr';
import { ApiResponse, PageableResponse, RepairService } from '../types/api';
import { API_BASE_URL, API_ENDPOINT, REPAIR_SERVICES_CACHE_KEY } from './constants';
import { useAuthFetch } from '../utils/auth';

export function useFetchRepairServices(
  pageNumber = 0,
  pageSize = 10,
  sortBy?: string,
  sortDirection?: 'asc' | 'desc'
) {

  const { authFetch } = useAuthFetch();
  
  const { data, error, isLoading, mutate } = useSWR<
    ApiResponse<PageableResponse<RepairService>>,
    Error
  >(
    [REPAIR_SERVICES_CACHE_KEY, pageNumber, pageSize, sortBy, sortDirection],
    () => fetchRepairServices(pageNumber, pageSize, authFetch, sortBy, sortDirection),
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

async function fetchRepairServices(
  page = 0,
  pageSize = 10,
  authFetch: (url: string, options?: RequestInit) => Promise<Response>,
  sortBy?: string,
  sortDirection?: 'asc' | 'desc',
): Promise<ApiResponse<PageableResponse<RepairService>>> {
  try {
    let url = `${API_BASE_URL}${API_ENDPOINT}?pageNumber=${page}&pageSize=${pageSize}`;

    if (sortBy) {
      url += `&sortBy=${sortBy}&sortDirection=${sortDirection || 'asc'}`;
    }

    const response = await authFetch(url);

    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }

    return response.json();
  } catch (error) {
    console.error('Error fetching repair services:', error);
    throw error;
  }
}

