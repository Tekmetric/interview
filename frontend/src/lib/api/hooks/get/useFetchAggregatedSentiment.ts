import { API_ENDPOINTS } from "@/config/api-endpoints";
import { useState, useEffect } from "react";

interface EvaluationResponse {
  evaluation: string;
  error?: string;
}

const failedToFetchDataMessage = "Failed to fetch data";
const unknownErrorMessage = "An unknown error occurred";
const noQueryProvidedMessage = "No query provided";

export const useFetchAggregatedSentiment = (symbols: string[]) => {
  const [data, setData] = useState<EvaluationResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (symbols.length === 0) {
      setError(noQueryProvidedMessage);
      return;
    }

    const fetchData = async () => {
      setLoading(true);
      setError(null);

      try {
        const query = symbols.map((symbol) => `query=${symbol}`).join("&");
        const response = await fetch(API_ENDPOINTS.getAggregatedInsight(query));
        const result = await response.json();

        if (!response.ok) {
          throw new Error(result.error || failedToFetchDataMessage);
        }

        setData(result);
      } catch (err) {
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError(unknownErrorMessage);
        }
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [symbols]);

  return { data, loading, error };
};
