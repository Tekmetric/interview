export interface Country {
  countryCode: string;
  name: string;
}

export interface CountryDetails {
  commonName: string,
  officialName: string,
  countryCode: string,
  region: string,
  borders: CountryDetails[]
}