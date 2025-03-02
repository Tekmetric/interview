"use client";

import { useState } from "react";
import { SymbolData } from "@/lib/api/hooks/get/useFetchSymbols";
import StockSearch from "./stock-search/stock-search";
import StockTable from "./stock-table/stock-table";

const PortfolioBuilder = () => {
  const [selectedOptions, setSelectedOptions] = useState<SymbolData[]>([]);
  const handleOptionSelect = (option: SymbolData) => {
    setSelectedOptions((prevOptions) => [...prevOptions, option]);
  };

  return (
    <>
      <StockSearch onOptionSelect={handleOptionSelect} />
      <StockTable data={selectedOptions} />
    </>
  );
};
export default PortfolioBuilder;
