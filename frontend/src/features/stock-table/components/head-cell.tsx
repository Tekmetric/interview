import { Indicator } from "@/lib/api/hooks/get/useFetchSymbolInsight";
import { SymbolData } from "@/lib/api/hooks/get/useFetchSymbols";

export type HeadCell = {
  disablePadding: boolean;
  id: keyof SymbolData | keyof Indicator;
  label: string;
  numeric: boolean;
  sortable: boolean;
};

export const headCells: readonly HeadCell[] = [
  {
    id: "description",
    numeric: false,
    disablePadding: true,
    label: "Description",
    sortable: true,
  },
  {
    id: "symbol",
    numeric: false,
    disablePadding: false,
    label: "Symbol",
    sortable: true,
  },
  {
    id: "type",
    numeric: false,
    disablePadding: false,
    label: "Type",
    sortable: false,
  },
  {
    id: "bestEntry",
    numeric: false,
    disablePadding: false,
    label: "Best Entry Point",
    sortable: false,
  },
  {
    id: "risk3Y",
    numeric: false,
    disablePadding: false,
    label: "Risk (3 Years)",
    sortable: false,
  },
  {
    id: "estimated3Y",
    numeric: false,
    disablePadding: false,
    label: "Estimated Growth (3 Years)",
    sortable: false,
  },
  {
    id: "yield",
    numeric: false,
    disablePadding: false,
    label: "Dividend Yield",
    sortable: false,
  },
  {
    id: "sentiment",
    numeric: false,
    disablePadding: false,
    label: "Market Sentiment",
    sortable: false,
  },
];
