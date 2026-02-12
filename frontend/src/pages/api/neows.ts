import type { APIRoute } from 'astro';
import { NeoWsBrowseResponseSchema } from '../../schemas/nasa';

// Disable prerendering for API routes https://docs.astro.build/en/guides/on-demand-rendering/
export const prerender = false;

export const GET: APIRoute = async ({ locals, url, request, params }) => {
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

    // Get page parameter from query string (default to 0)
    const page = url.searchParams.get('page') || '0';

    // Fetch from NASA NeoWs Browse API
    const response = await fetch(
      `https://api.nasa.gov/neo/rest/v1/neo/browse?page=${page}&api_key=${apiKey}`
    );

    if (!response.ok) {
      throw new Error('Failed to fetch NeoWs data from NASA');
    }

    const data = await response.json();

    // Validate response with Zod
    const validatedData = NeoWsBrowseResponseSchema.parse(data);

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
