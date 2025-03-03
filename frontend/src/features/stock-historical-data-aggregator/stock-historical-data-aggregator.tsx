import useFetchHistoricalData from "@/lib/api/hooks/get/useFetchHistoricalData";

import { Box, Typography } from "@mui/material";
import { format, subDays, subWeeks, subMonths, subYears } from "date-fns";
import { useState } from "react";
import { DATE_FORMAT } from "@/lib/constants/global";
import LoadingOverlay from "./components/loading-overlay";
import RangeSelect from "./components/range-select";
import DataChart from "./components/data-chart";

type StockHistoricalDataProps = {
  selectedSymbols: string[];
};

const StockHistoricalDataAggregator = ({
  selectedSymbols,
}: StockHistoricalDataProps) => {
  const [range, setRange] = useState("1Y");
  const calculateFromDate = (range: string) => {
    const toDate = new Date();
    switch (range) {
      case "1D":
        return format(subDays(toDate, 1), "yyyy-MM-dd");
      case "1W":
        return format(subWeeks(toDate, 1), "yyyy-MM-dd");
      case "1M":
        return format(subMonths(toDate, 1), "yyyy-MM-dd");
      case "6M":
        return format(subMonths(toDate, 6), "yyyy-MM-dd");
      case "1Y":
        return format(subYears(toDate, 1), "yyyy-MM-dd");
      case "3Y":
        return format(subYears(toDate, 3), "yyyy-MM-dd");
      case "MAX":
        return "2019-01-01";
      default:
        return format(subYears(toDate, 1), "yyyy-MM-dd");
    }
  };
  const from = calculateFromDate(range);
  const to = format(new Date(), "yyyy-MM-dd");
  const { data, isLoading, error } = useFetchHistoricalData(
    selectedSymbols,
    from,
    to
  );

  if (error) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        height="350px"
      >
        <Typography color="error">{error}</Typography>
      </Box>
    );
  }

  const chartData = data
    ? Object.keys(data).reduce((acc, symbol) => {
        data[symbol].forEach((entry) => {
          const date = format(new Date(entry.date), DATE_FORMAT);
          if (!acc[date]) {
            acc[date] = { date };
          }
          acc[date][symbol] = entry.close;
        });
        return acc;
      }, {} as { [key: string]: { date: string; [symbol: string]: number | string } })
    : {};

  const formattedChartData = Object.values(chartData);

  return (
    <Box
      sx={{
        width: "100%",
        height: 400,
        border: "1px solid",
        borderColor: (theme) => theme.palette.divider,
        pt: 2,
        pr: 2,
      }}
      display="flex"
      flexDirection="column"
      position={"relative"}
    >
      <LoadingOverlay isLoading={isLoading} />
      <RangeSelect
        isVisible={formattedChartData?.length > 0}
        range={range}
        setRange={setRange}
      />

      <Box height="350px">
        <DataChart
          formattedChartData={formattedChartData}
          selectedSymbols={selectedSymbols}
        />
      </Box>
    </Box>
  );
};

export default StockHistoricalDataAggregator;
