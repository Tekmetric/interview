import { useState } from 'react';
import { mutate as globalMutate } from 'swr';
import { ApiResponse, RepairService } from '../types/api';
import { API_BASE_URL, API_ENDPOINT, REPAIR_SERVICES_CACHE_KEY } from './constants';

export function useUpdateRepairService() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const updateService = async (id: number, serviceData: Omit<RepairService, 'id'>) => {
    setIsLoading(true);
    setError(null);

    try {
      const result = await updateRepairService(id, serviceData);

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
    updateService,
    isLoading,
    error,
  };
}

async function updateRepairService(
  id: number,
  serviceData: Omit<RepairService, 'id'>
): Promise<ApiResponse<RepairService>> {
  try {
    const response = await fetch(`${API_BASE_URL}${API_ENDPOINT}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(serviceData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || `API error: ${response.status}`);
    }

    return response.json();
  } catch (error) {
    console.error(`Error updating repair service ${id}:`, error);
    throw error;
  }
}
