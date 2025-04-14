import { useState } from 'react';
import { mutate as globalMutate } from 'swr';
import { ApiResponse } from '../types/api';
import { API_BASE_URL, API_ENDPOINT, REPAIR_SERVICES_CACHE_KEY } from './constants';

export function useDeleteRepairService() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const deleteService = async (id: number) => {
    setIsLoading(true);
    setError(null);

    try {
      const result = await deleteRepairService(id);
      globalMutate(key => Array.isArray(key) && key[0] === REPAIR_SERVICES_CACHE_KEY);
      return result;
    } catch (err) {
      const error = err instanceof Error ? err : new Error('An unknown error occurred');
      setError(error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    deleteService,
    isLoading,
    error,
  };
}

async function deleteRepairService(id: number): Promise<ApiResponse<void>> {
  try {
    const response = await fetch(`${API_BASE_URL}${API_ENDPOINT}/${id}`, {
      method: 'DELETE',
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || `API error: ${response.status}`);
    }

    return response.json();
  } catch (error) {
    console.error(`Error deleting repair service ${id}:`, error);
    throw error;
  }
}
