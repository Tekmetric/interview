export type ArtCrimeFilters = {
  crimeCategory?: string;
  maker?: string;
  materials?: string;
  period?: string;
  idInAgency?: string;
  referenceNumber?: string;
  measurements?: string;
  additionalData?: string;
};

export type ArtCrimeSorting = {
  sort_order?: 'desc' | 'asc'; // defaults to 'desc'
  // Note: at the moment 'publication' will return [], it defaults to 'modified'..and '_score' i'm not sure how it sorts
  sort_on?: 'modified' | 'publication' | '_score';
};

export type ArtCrimeQueryParams = {
  page?: number;
  pageSize?: number;
  title?: string;
} & ArtCrimeFilters &
  ArtCrimeSorting;

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
