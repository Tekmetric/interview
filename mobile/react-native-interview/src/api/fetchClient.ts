import { buildUrl } from './urlUtils';

type Method = 'GET' | 'POST' | 'PUT' | 'DELETE';

export async function apiRequest<T>(
  url: string,
  method: Method = 'GET',
  params?: Record<string, string | number | undefined>,
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
    const errorText = await res.text();
    throw new Error(`API Error: ${res.status} ${errorText}`);
  }

  return res.json();
}
