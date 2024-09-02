import React from "react";
import {
  Divider,
  FormControl,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  SelectChangeEvent,
  Typography,
} from "@mui/material";
import { MainContent } from "../components/Layout";
import { useCountries } from "../context/CountriesContext";
import { Country } from "../interfaces/country";
import { CountryInfo } from "../components/countryComponents/CountryInfo";
import { CountryHolidays } from "../components/countryComponents/CountryHolidays";

export const Dashboard = () => {
  const { countries } = useCountries();
  const [selectedCountry, setSelectedCountry] = React.useState<Country | null>(
    null
  );

  const handleChange = (event: SelectChangeEvent) => {
    const {
      target: { value },
    } = event;
    setSelectedCountry(countries.find((c) => c.countryCode === value) ?? null);
  };

  return (
    <MainContent>
      <Typography variant="subtitle1">
        Available Holidays information!
      </Typography>
      <FormControl sx={{ m: 1, width: 300 }}>
        <InputLabel id="select-country">Select Country</InputLabel>
        <Select
          value={selectedCountry?.name || ""}
          onChange={handleChange}
          label="Select Country"
          input={
            <OutlinedInput
              label="Select Country"
              value={selectedCountry?.name || ""}
            />
          }
          renderValue={(selected) => {
            if (selected.length === 0) {
              return <em>Select Country</em>;
            }

            return selected;
          }}
        >
          {countries.map(({ countryCode, name }) => (
            <MenuItem key={countryCode} value={countryCode}>
              {name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      {selectedCountry && (
        <>
          <CountryInfo country={selectedCountry} />
          <Divider />
          <CountryHolidays country={selectedCountry} />
        </>
      )}
    </MainContent>
  );
};
