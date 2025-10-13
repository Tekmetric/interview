import type { EntityLiveDataResponse, LiveDataParams } from '@/types/parks-api';
import { ParksApiError } from '@/types/parks-api-error';

const BASE_URL = 'https://api.themeparks.wiki/v1';

export async function getLiveData({ parkId, entityType }: LiveDataParams): Promise<EntityLiveDataResponse> {
  const response = await fetch(`${BASE_URL}/entity/${parkId}/live?entityType=${entityType}`);
  const responseData = await response.json();
  if (!response.ok) {
    throw new ParksApiError(responseData.error || 'Failed to fetch live data', response.status);
  }
  return responseData as Promise<EntityLiveDataResponse>;
}
