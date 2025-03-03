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
            "You are a helpful assistant for investment strategies. Provide a concise evaluation of the current portfolio based on the selected stock symbols.",
        },
        {
          role: "user",
          content: `Evaluate the current portfolio for the stock symbols ${symbolsString} in no more than 120 words. 
          Suggest other stocks that follow the same trend or have a similar risk profile.
          Suggest if some of the stocks in the portfolio should be removed based on the evaluation.`,
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

    return new Response(JSON.stringify({ evaluation: messageContent }), {
      status: 200,
      headers: { "Content-Type": "application/json" },
    });
  } catch (error) {
    console.error("Error fetching portfolio evaluation:", error);

    return new Response(
      JSON.stringify({ error: "Failed to fetch portfolio evaluation" }),
      {
        status: 500,
        headers: { "Content-Type": "application/json" },
      }
    );
  }
}
