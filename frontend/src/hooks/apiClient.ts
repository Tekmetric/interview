import {
  HttpError,
  InvalidResponseError,
  NetworkError,
  TimeoutError,
} from './errors';

// if this was not a dummy API we would probably keep this in an
// env file or similar configuration with auth.
const BASE_URL = 'https://dummyjson.com';

export interface FetchJsonOptions<T> {
  validate: (value: unknown) => value is T;
  signalTimeoutMs?: number;
  query?: Record<string, string | number | undefined>;
}

function buildUrl(
  path: string,
  query?: Record<string, string | number | undefined>
): string {
  const url = new URL(path, BASE_URL);

  if (query) {
    for (const [key, value] of Object.entries(query)) {
      if (value !== undefined) {
        url.searchParams.set(key, String(value));
      }
    }
  }

  return url.toString();
}

export async function fetchJson<T>(
  path: string,
  options: FetchJsonOptions<T>
): Promise<T> {
  const { validate, signalTimeoutMs = 8000, query } = options;
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), signalTimeoutMs);

  try {
    const response = await fetch(buildUrl(path, query), {
      signal: controller.signal,
    });

    if (!response.ok) {
      throw new HttpError(response.status);
    }

    let data: unknown;

    try {
      data = await response.json();
    } catch {
      throw new InvalidResponseError('Response is not valid JSON');
    }

    if (!validate(data)) {
      throw new InvalidResponseError('Response does not match expected shape');
    }

    return data;
  } catch (error) {
    if (error instanceof HttpError || error instanceof InvalidResponseError) {
      throw error;
    }

    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new TimeoutError();
    }

    throw new NetworkError(
      error instanceof Error ? error.message : 'Network error'
    );
  } finally {
    clearTimeout(timeoutId);
  }
}
