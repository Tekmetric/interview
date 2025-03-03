"use client";

import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Autocomplete from "@mui/material/Autocomplete";
import Paper, { PaperProps } from "@mui/material/Paper";
import Grid2 from "@mui/material/Grid2";
import Typography from "@mui/material/Typography";
import LinearProgress from "@mui/material/LinearProgress";
import parse from "autosuggest-highlight/parse";
import { debounce } from "@mui/material/utils";
import useFetchSymbols, {
  SymbolData,
} from "@/lib/api/hooks/get/useFetchSymbols";
import { useEffect, useMemo, useState } from "react";

const CustomPaper = (props: PaperProps) => {
  return <Paper {...props}>{props.children}</Paper>;
};

type StockSearchProps = {
  onOptionSelect: (option: SymbolData) => void;
};

const StockSearch: React.FC<StockSearchProps> = ({ onOptionSelect }) => {
  const [inputValue, setInputValue] = useState("");
  const [debouncedInputValue, setDebouncedInputValue] = useState("");

  const { data, isLoading, error } = useFetchSymbols(debouncedInputValue);
  const options: SymbolData[] = data ? data.result : [];

  const debouncedFetch = useMemo(
    () =>
      debounce((value: string) => {
        if (value.length >= 2) {
          setDebouncedInputValue(value);
        } else {
          setDebouncedInputValue("");
        }
      }, 400),
    []
  );

  useEffect(() => {
    debouncedFetch(inputValue);
    return () => {
      debouncedFetch.clear();
    };
  }, [inputValue, debouncedFetch]);

  return (
    <Box sx={{ position: "relative", maxWidth: 300, width: "100%" }}>
      <Autocomplete
        getOptionLabel={(option) => option.description}
        filterOptions={(x) => x}
        slots={{
          paper: CustomPaper,
        }}
        options={options || []}
        autoComplete
        includeInputInList
        filterSelectedOptions
        noOptionsText="Nothing found"
        onInputChange={(_event, newInputValue) => {
          setInputValue(newInputValue);
        }}
        renderInput={(params) => (
          <TextField
            {...params}
            label="Search for a company or symbol"
            fullWidth
            error={!!error}
            helperText={error ? "Error fetching data" : ""}
          />
        )}
        renderOption={(optionProps, option: SymbolData) => {
          const matches = Array.from(
            option.description.matchAll(new RegExp(inputValue, "gi"))
          );

          const parts = parse(
            option.description,
            matches.map((match) => [match.index, match.index + match[0].length])
          );

          return (
            <li
              {...optionProps}
              key={`${option.symbol}-${option.description}`}
              onClick={() => onOptionSelect(option)}
            >
              <Grid2 container sx={{ alignItems: "center" }}>
                <Grid2 sx={{ wordWrap: "break-word" }}>
                  {parts.map(
                    (
                      part: { text: string; highlight: boolean },
                      index: number
                    ) => (
                      <Box
                        key={index}
                        component="span"
                        sx={{
                          fontWeight: part.highlight
                            ? "fontWeightBold"
                            : "fontWeightRegular",
                        }}
                      >
                        {part.text}
                      </Box>
                    )
                  )}
                  <Typography variant="body2" sx={{ color: "text.secondary" }}>
                    {option.symbol}
                  </Typography>
                </Grid2>
              </Grid2>
            </li>
          );
        }}
      />
      {isLoading && (
        <LinearProgress
          sx={{ position: "absolute", bottom: 0, left: 0, right: 0 }}
        />
      )}
    </Box>
  );
};

export default StockSearch;
