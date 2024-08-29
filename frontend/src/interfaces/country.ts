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

export interface HolidayInfo {
  date: string;
  localName: string;
  name: string;
  countryCode: string;
  fixed: boolean;
  global: boolean;
  counties: string[] | null
  launchYear: number | null;
  types: HolidayTypes[];
}

enum HolidayTypes {
  Public = 'Public',
  Bank = 'Bank',
  School = 'School',
  Authorities = 'Authorities',
  Optional = 'Optional',
  Observance = 'Observance'
}