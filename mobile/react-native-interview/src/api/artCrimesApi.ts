import { FBI_API_BASE_URL } from '../config/constants';
import { ArtCrimeQueryParams } from '../types/artCrime';
import { apiRequest } from './fetchClient';

const ART_CRIME_ENDPOINT = '/@artcrimes';

type ArtCrimeField = string | null;

// Note: all item properties might be null (from the online API schema) but we can assume that the API will return a valid item uid
export type ArtCrime = {
  uid: string;
  title: ArtCrimeField;
  description: ArtCrimeField;
  images: { original: ArtCrimeField; thumb: ArtCrimeField }[] | null;
  crimeCategory: ArtCrimeField;
  maker: ArtCrimeField;
  materials: ArtCrimeField;
  measurements: ArtCrimeField;
  period: ArtCrimeField;
  additionalData: ArtCrimeField;
  modified: ArtCrimeField;
  publication: ArtCrimeField;
  path: ArtCrimeField;
  referenceNumber: ArtCrimeField;
};

export type FBIArtCrimeResponse = {
  items: ArtCrime[];
  total: number;
  page: number;
};

export const fetchArtCrimes = async (
  params: ArtCrimeQueryParams | undefined,
): Promise<FBIArtCrimeResponse> => {
  return await apiRequest<FBIArtCrimeResponse>(
    `${FBI_API_BASE_URL}${ART_CRIME_ENDPOINT}`,
    'GET',
    params,
  );
};
