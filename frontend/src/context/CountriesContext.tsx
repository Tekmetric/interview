import React, { createContext, useContext, useEffect, useState } from "react";

import { useQueryClient } from "react-query";
import { Country } from "../interfaces/country";
import { AlertTypes, useAlert } from "./AlertContext";

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
  const { setAlert } = useAlert();
  const queryClient = useQueryClient();

  useEffect(() => {
    getCountries();
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
      setAlert(`Get list of all available countries failed`, AlertTypes.ERROR);
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
