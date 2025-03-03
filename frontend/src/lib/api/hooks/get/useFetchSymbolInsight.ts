import { API_ENDPOINTS } from "@/config/api-endpoints";
import { useState, useEffect } from "react";

export type Indicator = {
  bestEntry: string;
  risk3Y: string;
  estimated3Y: string;
  yield: string;
  sentiment: string;
};

export type SymbolInsight = {
  symbol: string;
  indicators: Indicator;
};

export type UseFetchSymbolInsightResult = {
  data: SymbolInsight[] | null;
  isLoading: boolean;
  error: string | null;
};

const failedToFetchDataMessage = "Failed to fetch data";
const unknownErrorMessage = "An unknown error occurred";

const useFetchSymbolInsight = (
  symbols: string[]
): UseFetchSymbolInsightResult => {
  const [data, setData] = useState<SymbolInsight[] | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const query = symbols.map((symbol) => `query=${symbol}`).join("&");
        const response = await fetch(API_ENDPOINTS.getSymbolInsight(query));

        if (!response.ok) {
          throw new Error(failedToFetchDataMessage);
        }
        const result = await response.json();
        setData(result);
      } catch (err) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError(unknownErrorMessage);
        }
      } finally {
        setIsLoading(false);
      }
    };

    if (symbols.length > 0) {
      fetchData();
    } else {
      setIsLoading(false);
    }
  }, [symbols]);

  return { data, isLoading, error };
};

export default useFetchSymbolInsight;
