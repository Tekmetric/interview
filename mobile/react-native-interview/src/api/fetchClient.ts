import { buildUrl } from './urlUtils';

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE';

export class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
    public statusText: string,
    public response?: any,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

const MAX_RETRIES = 3;
const RETRY_DELAY = 1000; // 1 second

async function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

export async function apiRequest<T>(
  url: string,
  method: Method = 'GET',
  params?: Record<string, string | number | undefined>,
  retryCount = 0,
): Promise<T> {
  const requestUrl = buildUrl(url, method === 'GET' ? params : undefined);
  console.log({ requestUrl });

  const res = await fetch(requestUrl, {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
    body: method !== 'GET' ? JSON.stringify(params) : undefined,
  });

  if (!res.ok) {
    let errorResponse;
    try {
      errorResponse = await res.json();
    } catch {
      errorResponse = await res.text();
    }

    const error = new ApiError(
      `API Error: ${res.status} ${res.statusText}`,
      res.status,
      res.statusText,
      errorResponse,
    );

    if (retryCount < MAX_RETRIES && res.status >= 500) {
      await delay(RETRY_DELAY * Math.pow(2, retryCount));
      return apiRequest<T>(url, method, params, retryCount + 1);
    }

    throw error;
  }

  return res.json();
}
