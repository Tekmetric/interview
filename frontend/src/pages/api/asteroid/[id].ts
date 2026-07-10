import type { APIRoute } from 'astro';
import { AsteroidDetailSchema } from '../../../schemas/nasa';

// Disable prerendering for API routes
export const prerender = false;

export const GET: APIRoute = async ({ locals, params }) => {
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

    // Get asteroid ID from route parameter
    const asteroidId = params.id;

    if (!asteroidId) {
      return new Response(
        JSON.stringify({ error: 'Asteroid ID is required' }),
        {
          status: 400,
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );
    }

    // Fetch from NASA NeoWs Lookup API
    const response = await fetch(
      `https://api.nasa.gov/neo/rest/v1/neo/${asteroidId}?api_key=${apiKey}`
    );

    if (!response.ok) {
      if (response.status === 404) {
        return new Response(
          JSON.stringify({ error: 'Asteroid not found' }),
          {
            status: 404,
            headers: {
              'Content-Type': 'application/json',
            },
          }
        );
      }
      throw new Error('Failed to fetch asteroid data from NASA');
    }

    const data = await response.json();

    // Validate response with Zod
    const validatedData = AsteroidDetailSchema.parse(data);

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
