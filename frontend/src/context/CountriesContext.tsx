import React, { createContext, useContext, useEffect, useState } from "react";

import { useQueryClient } from "react-query";
import { Country } from "../interfaces/country";

interface CountriesContextProps {
  countries: Country[];
}

export const CountriesContext = createContext<CountriesContextProps>({
  countries: []
});

export const CountriesContextProvider: React.FC<{
  children: React.ReactNode;
}> = ({ children }) => {
  const [countries, setCountries] = useState<Country[]>([]);
  const queryClient = useQueryClient();

  useEffect(() => {
    getCountries();
  }, []);

  const getCountries = async () => {
    setCountries([]);

    try {
      const countries: Country[] = await queryClient.fetchQuery(
        ["getCountries"],
        async () => {
          const response = await fetch(
            `https://date.nager.at/api/v3/AvailableCountries`
          )
          return await response.json();
        }
      );
      setCountries(countries);
    } catch (error) {
      console.log(`Get countries failed. Error: ${JSON.stringify(error)}`);
    }
  };

  return (
    <CountriesContext.Provider
      value={{ countries }}
    >
      {children}
    </CountriesContext.Provider>
  );
};

export const useCountries = () => useContext(CountriesContext);
