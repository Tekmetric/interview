import { FBI_API_BASE_URL } from '../config/constants';
import { ArtCrimeQueryParams, FBIArtCrimeResponse } from '@/types/artCrime';
import { apiRequest } from './fetchClient';

const ART_CRIME_ENDPOINT = '/@artcrimes';

export const fetchArtCrimes = async (
  params: ArtCrimeQueryParams | undefined,
): Promise<FBIArtCrimeResponse> => {
  return await apiRequest<FBIArtCrimeResponse>(
    `${FBI_API_BASE_URL}${ART_CRIME_ENDPOINT}`,
    'GET',
    params,
  );
};
