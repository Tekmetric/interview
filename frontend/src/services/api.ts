import { ApiResponse, PageableResponse, RepairService } from '../types/api';

export async function fetchRepairServices(
  page = 0,
  pageSize = 10,
  sortBy?: string,
  sortDirection?: 'asc' | 'desc'
): Promise<ApiResponse<PageableResponse<RepairService>>> {
  try {
    const API_URL = process.env.REACT_APP_API_SERVER_URL;

    let url = `${API_URL}/api/repair-services?pageNumber=${page}&pageSize=${pageSize}`;

    if (sortBy) {
      url += `&sortBy=${sortBy}&sortDirection=${sortDirection || 'asc'}`;
    }

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`API error: ${response.status}`);
    }

    return response.json();
  } catch (error) {
    console.error('Error fetching repair services:', error);
    throw error;
  }
}
