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
