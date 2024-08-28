import React from "react";
import {
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

export const Dashboard = () => {
  const { countries } = useCountries();
  const [selectedCountry, setSelectedCountry] = React.useState<Country | null>(
    null
  );

  const handleChange = (event: SelectChangeEvent) => {
    console.log(event.target);
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
          labelId="select-country"
          value={selectedCountry?.name || ""}
          onChange={handleChange}
          input={<OutlinedInput label="Select Country" />}
        >
          {countries.map(({ countryCode, name }) => (
            <MenuItem key={countryCode} value={countryCode}>
              {name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      {selectedCountry && (
        <CountryInfo countryCode={selectedCountry.countryCode} />
      )}
    </MainContent>
  );
};
