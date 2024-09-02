import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import Grid from "@mui/material/Grid2";
import React, { useEffect, useState } from "react";
import { Country, HolidayInfo } from "../../interfaces/country";
import { useQuery } from "react-query";
import { Loading } from "../common/Loading";
import { AlertTypes, useAlert } from "../../context/AlertContext";
import { HolidayEntry } from "./HolidayEntry";
import { YearNavigation } from "./YearNavigation";
import { nanoid } from "nanoid";

interface CountryHolidaysProps {
  country: Country;
}

export const CountryHolidays: React.FC<CountryHolidaysProps> = ({
  country,
}) => {
  const [year, setYear] = useState<number>(new Date().getFullYear());
  const { setAlert } = useAlert();

  const holidaysQuery = useQuery(
    "holidays",
    async () => {
      try {
        const response = await fetch(
          `https://date.nager.at/api/v3/PublicHolidays/${year}/${country?.countryCode}`
        );
        return await response.json();
      } catch (error) {
        setAlert(
          `Could not fetch ${country?.name} public holidays`,
          AlertTypes.ERROR
        );
      }
    },
    { refetchOnWindowFocus: false }
  );

  useEffect(() => {
    holidaysQuery.refetch();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [country, year]);

  return (
    <Box sx={{ flexGrow: 1, marginTop: "20px" }}>
      {holidaysQuery.isFetching ? (
        <Loading />
      ) : (
        <Grid container spacing={2}>
          {holidaysQuery.data ? (
            <>
              <Typography
                variant="h4"
                sx={{ width: "100%" }}
              >{`Public Holidays in ${country.name} ${year}`}</Typography>
              <YearNavigation year={year} setYear={setYear} />
              <TableContainer component={Paper}>
                <Table sx={{ minWidth: 650 }} size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <Box sx={{ fontWeight: "bold" }}>Date</Box>
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ fontWeight: "bold" }}>Local name</Box>
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ fontWeight: "bold" }}>Name</Box>
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ fontWeight: "bold" }}>Holiday Type</Box>
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ fontWeight: "bold" }}>Global</Box>
                      </TableCell>
                      <TableCell align="right">
                        <Box sx={{ fontWeight: "bold" }}>Counties</Box>
                      </TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {holidaysQuery?.data?.map((holiday: HolidayInfo) => (
                      <HolidayEntry holiday={holiday} key={nanoid()}/>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </>
          ) : (
            <Typography>
              {`No public holidays available for ${country?.name} ${year}!`}
            </Typography>
          )}
        </Grid>
      )}
    </Box>
  );
};
