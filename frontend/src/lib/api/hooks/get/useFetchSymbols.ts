import { useState, useEffect } from "react";

export type SymbolData = {
  description: string;
  displaySymbol: string;
  symbol: string;
  type: string;
};

interface ApiSymbolData {
  count: number;
  result: SymbolData[];
}

const useFetchSymbols = (query: string) => {
  const [data, setData] = useState<ApiSymbolData | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!query) {
      setData(null);
      setIsLoading(false);
      return;
    }

    const fetchData = async () => {
      setIsLoading(true);
      setError(null);

      const url = `/api/fin/get-symbols?query=${encodeURIComponent(query)}`;

      try {
        const response = await fetch(url);
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
  }, [query]);

  return { data, isLoading, error };
};

export default useFetchSymbols;
