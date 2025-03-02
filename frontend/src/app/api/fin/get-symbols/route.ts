import { NextRequest } from "next/server";

export async function GET(request: NextRequest) {
  const { searchParams } = new URL(request.url);
  const query = searchParams.get("query");

  if (!query) {
    return new Response(JSON.stringify({ error: "Query parameter is required" }), {
      status: 400,
      headers: { "Content-Type": "application/json" },
    });
  }
  
  const url = new URL("https://finnhub.io/api/v1/search");
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
      JSON.stringify({ error: "Failed to search for company name" }),
      {
        status: 500,
        headers: { "Content-Type": "application/json" },
        statusText: "Internal Server Error - Failed to search for company name",
      }
    );
  }
}
