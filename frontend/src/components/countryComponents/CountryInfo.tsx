import { Box, Typography } from "@mui/material";
import Grid from "@mui/material/Grid2";
import React, { useEffect, useState } from "react";
import { Country, CountryDetails } from "../../interfaces/country";
import { useQueryClient } from "react-query";
import { Loading } from "../common/Loading";
import { AlertTypes, useAlert } from "../../context/AlertContext";

interface CountryInfoProps {
  country: Country;
}

export const CountryInfo: React.FC<CountryInfoProps> = ({ country }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [countryDetails, setCountryDetails] = useState<CountryDetails | null>(
    null
  );
  const { setAlert } = useAlert();
  const queryClient = useQueryClient();

  useEffect(() => {
    getCountryDetails();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [country]);

  const getCountryDetails = async () => {
    setIsLoading(true);
    setCountryDetails(null);

    try {
      const countryInfo: CountryDetails = await queryClient.fetchQuery(
        ["getCountryInfo"],
        async () => {
          const response = await fetch(
            `https://date.nager.at/api/v3/CountryInfo/${country?.countryCode}`
          );
          return await response.json();
        }
      );
      setCountryDetails(countryInfo);
    } catch (error) {
      setAlert(`Could not fetch info about ${country?.name}`, AlertTypes.ERROR);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Box sx={{ flexGrow: 1, margin: '20px 0 ' }}>
      {isLoading ? (
        <Loading />
      ) : (
        <Grid container spacing={2}>
          {countryDetails ? (
            <>
              <Grid size="grow">
                <Typography>Common Name:</Typography>
                <Typography>Official Name:</Typography>
                <Typography>Region:</Typography>
                <Typography>Country Code:</Typography>
                <Typography>Borders:</Typography>
              </Grid>
              <Grid size={10}>
                <Typography>{countryDetails?.commonName ?? "-"}</Typography>
                <Typography>{countryDetails?.officialName ?? "-"}</Typography>
                <Typography>{countryDetails?.region ?? "-"}</Typography>
                <Typography>{countryDetails?.countryCode ?? "-"}</Typography>
                <Typography>
                  {countryDetails?.borders !== null &&
                  countryDetails.borders?.length > 0
                    ? countryDetails?.borders?.map((border, index) => (
                        <span
                          key={`${countryDetails.countryCode}-${border?.countryCode}`}
                        >
                          {index ===
                          (countryDetails?.borders &&
                            countryDetails?.borders?.length - 1)
                            ? border?.commonName
                            : border?.commonName.concat(", ")}
                        </span>
                      ))
                    : "Missing Country borders information"}
                </Typography>
              </Grid>
            </>
          ) : (
            <Typography>
              {`No Information available for ${country?.name}!`}
            </Typography>
          )}
        </Grid>
      )}
    </Box>
  );
};
