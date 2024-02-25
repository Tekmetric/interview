export interface ManufacturersRespData {
  Count: number;
  Message: string;
  Results: {
    Country: string;
    Mfr_CommonName: string;
    Mfr_ID: number;
    Mfr_Name: string;
    VechicleTypes: {
      IsPrimary: boolean;
      Name: string;
    }[];
  }[];
}
