import * as qs from 'qs';

type Request = {
  url: string;
  params?: { [key in string]: string | number };
  headers?: { [key in string]: string };
};

export const request = async ({ url, params }: Request) => {
  const queryString = qs.stringify(params, { encode: false });
  const finalUrl = queryString ? `${url}?${queryString}` : url;

  const response = await fetch(finalUrl, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error(response.statusText ?? response.status.toString());
  }

  return response.json();
};
