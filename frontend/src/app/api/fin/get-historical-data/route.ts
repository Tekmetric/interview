import { NextRequest } from "next/server";
import { API_ENDPOINTS } from "@/config/api-endpoints";
import { API_STATUS_TOKENS } from "@/config/api-status-tokens";

type CandleData = {
  date: string; // Date in ISO format
  close: number; // Close price
  high: number; // High price
  low: number; // Low price
  open: number; // Open price
  volume: number; // Volume
  adjClose?: number; // Adjusted close price (optional)
  adjHigh?: number; // Adjusted high price (optional)
  adjLow?: number; // Adjusted low price (optional)
  adjOpen?: number; // Adjusted open price (optional)
  adjVolume?: number; // Adjusted volume (optional)
  divCash?: number; // Dividend cash (optional)
  splitFactor?: number; // Split factor (optional)
};

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const symbols = searchParams.get("symbols");
  const from = searchParams.get("from");
  const to = searchParams.get("to");

  if (!symbols) {
    return new Response(
      JSON.stringify({ error: API_STATUS_TOKENS.errors.queryRequired }),
      {
        status: 400,
        headers: { "Content-Type": "application/json" },
      }
    );
  }

  const symbolArray = symbols.split(",");
  const now = new Date().toISOString().split("T")[0];
  const oneYearAgo = new Date();
  oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);

  const fromDate = from
    ? new Date(from).toISOString().split("T")[0]
    : oneYearAgo.toISOString().split("T")[0];
  const toDate = to ? new Date(to).toISOString().split("T")[0] : now;

  try {
    const data: { [symbol: string]: CandleData[] } = {};

    const fetchPromises = symbolArray.map(async (symbol) => {
      const url = new URL(API_ENDPOINTS.getHistoricalData(symbol));
      url.searchParams.set("startDate", fromDate);
      url.searchParams.set("endDate", toDate);

      const response = await fetch(url.toString(), {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Token ${process.env.TIINGO_API_KEY || ""}`,
        },
      });

      if (!response.ok) {
        throw new Error(
          `Error fetching data for symbol ${symbol}: ${response.statusText}`
        );
      }

      const result: CandleData[] = await response.json();
      data[symbol] = result;
    });

    await Promise.all(fetchPromises);

    return new Response(JSON.stringify(data), {
      headers: { "Content-Type": "application/json" },
    });
  } catch (error) {
    console.error("Error fetching historical data:", error);

    return new Response(
      JSON.stringify({ error: API_STATUS_TOKENS.errors.searchFailed }),
      {
        status: 500,
        headers: { "Content-Type": "application/json" },
        statusText: API_STATUS_TOKENS.errors.searchFailed,
      }
    );
  }
}
