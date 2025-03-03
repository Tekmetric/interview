import { useState, useEffect } from "react";

interface HistoricalData {
  date: string;
  close: number;
  high: number;
  low: number;
  open: number;
  volume: number;
  adjClose: number;
  adjHigh: number;
  adjLow: number;
  adjOpen: number;
  adjVolume: number;
  divCash: number;
  splitFactor: number;
}

const useFetchHistoricalData = (
  symbols: string[],
  from?: string,
  to?: string
) => {
  const [data, setData] = useState<{
    [symbol: string]: HistoricalData[];
  } | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!symbols.length) {
      setData(null);
      setIsLoading(false);
      return;
    }

    const fetchData = async () => {
      setIsLoading(true);
      setError(null);

      const url = new URL(
        "/api/fin/get-historical-data",
        window.location.origin
      );
      url.searchParams.append("symbols", symbols.join(","));
      if (from) url.searchParams.append("from", from);
      if (to) url.searchParams.append("to", to);

      try {
        const response = await fetch(url.toString());
        if (!response.ok) {
          throw new Error(`Error: ${response.statusText}`);
        }
        const result = await response.json();
        setData(result);
      } catch (err: unknown) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError("An unknown error occurred");
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [symbols, from, to]);

  return { data, isLoading, error };
};

export default useFetchHistoricalData;
