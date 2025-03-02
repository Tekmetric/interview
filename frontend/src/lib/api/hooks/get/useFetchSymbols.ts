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
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!query) {
      setData(null);
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      setLoading(true);
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
        setLoading(false);
      }
    };

    fetchData();
  }, [query]);

  return { data, loading, error };
};

export default useFetchSymbols;
