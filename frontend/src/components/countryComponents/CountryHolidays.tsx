import { Card, Typography } from "@mui/material";
import Grid from "@mui/material/Grid2";
import React, { useEffect, useState } from "react";
import { Country, HolidaysInfo } from "../../interfaces/country";
import { useQueryClient } from "react-query";
import { Loading } from "../common/Loading";
import { AlertTypes, useAlert } from "../../context/AlertContext";

interface CountryHolidaysProps {
  country: Country;
}

export const CountryHolidays: React.FC<CountryHolidaysProps> = ({
  country,
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const [countryHolidays, setCountryHolidays] = useState<HolidaysInfo[] | null>(null);
  const [year, setYear] = useState<string>(new Date().getFullYear().toString());
  const { setAlert } = useAlert();
  const queryClient = useQueryClient();

  useEffect(() => {
    getCountryHolidays();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [country]);

  const getCountryHolidays = async () => {
    setIsLoading(true);
    setCountryHolidays(null);

    try {
      const countryHolidays: HolidaysInfo[] = await queryClient.fetchQuery(
        ["getCountryHolidays"],
        async () => {
          const response = await fetch(
            `https://date.nager.at/api/v3/PublicHolidays/${year}/${country?.countryCode}`
          );
          return await response.json();
        }
      );
      setCountryHolidays(countryHolidays);
    } catch (error) {
      setAlert(
        `Could not fetch ${country?.name} public holidays`,
        AlertTypes.ERROR
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Card sx={{ flexGrow: 1 }}>
      {isLoading ? (
        <Loading />
      ) : (
        <Grid container spacing={2}>
          {countryHolidays ? (
            <>{JSON.stringify(countryHolidays)}</>
          ) : (
            <Typography>
              {`No public holidays available for ${country?.name}!`}
            </Typography>
          )}
        </Grid>
      )}
    </Card>
  );
};
