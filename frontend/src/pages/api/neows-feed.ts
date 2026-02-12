import type { APIRoute } from 'astro';
import { NeoWsFeedResponseSchema } from '../../schemas/nasa';

// Disable prerendering for API routes https://docs.astro.build/en/guides/on-demand-rendering/
export const prerender = false;

export const GET: APIRoute = async ({ locals, url }) => {
  try {
    // Access the Cloudflare runtime environment
    const runtime = locals.runtime as any;
    const apiKey = runtime?.env?.NASA_API_KEY;

    if (!apiKey) {
      return new Response(
        JSON.stringify({ error: 'NASA_API_KEY not configured' }),
        {
          status: 500,
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
    }

    // Get date parameters from query string
    const startDate = url.searchParams.get('start_date');
    const endDate = url.searchParams.get('end_date');

    if (!startDate) {
      return new Response(
        JSON.stringify({ error: 'start_date parameter is required (YYYY-MM-DD format)' }),
        {
          status: 400,
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
    }

    // Validate date format (basic check)
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    if (!dateRegex.test(startDate) || (endDate && !dateRegex.test(endDate))) {
      return new Response(
        JSON.stringify({ error: 'Invalid date format. Use YYYY-MM-DD' }),
        {
          status: 400,
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
    }

    // Build NASA API URL
    let nasaUrl = `https://api.nasa.gov/neo/rest/v1/feed?start_date=${startDate}&api_key=${apiKey}`;
    if (endDate) {
      nasaUrl += `&end_date=${endDate}`;
    }

    // Fetch from NASA NeoWs Feed API
    const response = await fetch(nasaUrl);

    if (!response.ok) {
      throw new Error('Failed to fetch NeoWs Feed data from NASA');
    }

    const data = await response.json();

    // Validate response with Zod
    const validatedData = NeoWsFeedResponseSchema.parse(data);

    return new Response(JSON.stringify(validatedData), {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  } catch (error) {
    return new Response(
      JSON.stringify({
        error: error instanceof Error ? error.message : 'An error occurred',
      }),
      {
        status: 500,
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  }
};
