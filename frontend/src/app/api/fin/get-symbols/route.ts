import { API_ENDPOINTS } from "@/config/api-endpoints";
import { API_STATUS_TOKENS } from "@/config/api-status-tokens";
import { NextRequest } from "next/server";

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const query = searchParams.get("query");

  if (!query) {
    return new Response(
      JSON.stringify({ error: API_STATUS_TOKENS.errors.queryRequired }),
      {
        status: 400,
        headers: { "Content-Type": "application/json" },
      }
    );
  }

  const url = new URL(API_ENDPOINTS.getSymbols);
  url.searchParams.set("q", query);
  url.searchParams.set("exchange", "US");

  try {
    const response = await fetch(url.toString(), {
      headers: { "X-Finnhub-Token": process.env.FINNHUB_API_KEY || "" },
    });

    const data = await response.json();
    const transformed = { ...data, source: "Finnhub - proxied-through-nextjs" };

    return new Response(JSON.stringify(transformed), {
      headers: { "Content-Type": "application/json" },
    });
  } catch (error) {
    console.error("Error searching for company name:", error);

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
