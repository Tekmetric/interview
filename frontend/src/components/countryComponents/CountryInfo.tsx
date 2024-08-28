import { Card } from "@mui/material";
import React, { useEffect, useState } from "react";
import { CountryDetails } from '../../interfaces/country';
import { useQueryClient } from "react-query";

interface CountryInfoProps {
  countryCode: string;
}

export const CountryInfo: React.FC<CountryInfoProps> = ({
  countryCode,
}) => {
  const [countryDetails, setCountryDetails] = useState<CountryDetails | null>(null);
  const queryClient = useQueryClient();
  
  useEffect(() => {
    getCountryDetails();
  }, [countryCode]);

  const getCountryDetails = async () => {
    setCountryDetails(null);

    try {
      const country: CountryDetails = await queryClient.fetchQuery(
        ["getCountryInfo"],
        async () => {
          const response = await fetch(
            `https://date.nager.at/api/v3/CountryInfo/${countryCode}`
          )
          return await response.json();
        }
      );
      setCountryDetails(country);
    } catch (error) {
      console.log(`Get Country Info failed. Error: ${JSON.stringify(error)}`);
    }
  }

  return <Card>{JSON.stringify(countryDetails)}</Card>;
};
