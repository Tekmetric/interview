export interface Country {
  countryCode: string;
  name: string;
}

export interface CountryDetails {
  commonName: string;
  officialName: string;
  countryCode: string;
  region: string;
  borders: CountryDetails[] | null
}

export interface HolidaysInfo {
  date: string;
  localName: string;
  name: string;
  countryCode: string;
  global: boolean;
  counties: string[]
  launchYear: number;
  types: string[];
}