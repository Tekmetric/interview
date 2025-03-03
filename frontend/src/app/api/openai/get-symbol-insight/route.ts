import { API_STATUS_TOKENS } from "@/config/api-status-tokens";
import { NextRequest } from "next/server";
import OpenAI from "openai";

const client = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
});

const API_MODEL = "gpt-4o";

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const symbols = searchParams.getAll("query");

  if (!symbols || symbols.length === 0) {
    return new Response(
      JSON.stringify({ error: API_STATUS_TOKENS.errors.queryRequired }),
      {
        status: 400,
        headers: { "Content-Type": "application/json" },
      }
    );
  }

  const symbolsString = symbols.join(", ");

  try {
    const response = await client.chat.completions.create({
      model: API_MODEL,
      messages: [
        {
          role: "system",
          content:
            "You are a helpful assistant for investment strategies. Your answer should limit only to the provided format and nothing else. Correlate data from different sources, you estimate based on the historical price trajectory and what you can find on the internet.",
        },
        {
          role: "user",
          content: `Provide the following indicators for the stock symbols ${symbolsString} based on the historical price trajectory and what you can find on the internet:
            1. Best Entry Point: {price as number}
            2. Estimated Risk Over the Next 3 Years: {number between 1 and 9}
            3. Estimated Growth Rate {percentage}
            4. Dividend Yield for next year {percentage}
            5. Market Sentiment {relevant information but condensed to maxs 2 senteces, 80 characters}`,
        },
        {
          role: "user",
          content: `Make sure the answer is in a valid JSON format but returned as a string that can be directly parsed by JSON.parse(), following the structure:
        [
            {
            "symbol": "string",
            "indicators": {
                "bestEntry": "string",
                "risk3Y": "string",
                "estimated3Y": "string",
                "yield": "string",
                "sentiment": "string"
                 } 
            },
            {
            "symbol": "string",
            "indicators": {
                "bestEntry": "string",
                "risk3Y": "string",
                "estimated3Y": "string",
                "yield": "string",
                "sentiment": "string"
            }
        ]`,
        },
      ],
    });

    const messageContent = response.choices[0]?.message?.content;

    if (!messageContent) {
      return new Response(
        JSON.stringify({
          error: API_STATUS_TOKENS.errors.openAINoResponse,
        }),
        {
          status: 500,
          headers: { "Content-Type": "application/json" },
        }
      );
    }

    // Clean up the response content
    const extractJsonFromString = (str: string) => {
      const match = str.match(/(\{[\s\S]*\}|\[[\s\S]*\])/);
      return match ? match[0] : "[]";
    };
    const jsonString = extractJsonFromString(messageContent);
    const indicators = JSON.parse(jsonString);

    return new Response(JSON.stringify(indicators), {
      status: 200,
      headers: { "Content-Type": "application/json" },
    });
  } catch (error) {
    console.error("Error fetching stock indicators:", error);

    return new Response(
      JSON.stringify({ error: "Failed to fetch stock indicators" }),
      {
        status: 500,
        headers: { "Content-Type": "application/json" },
      }
    );
  }
}
