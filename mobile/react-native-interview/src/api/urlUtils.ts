export const buildUrl = (url: string, params?: Record<string, string | number | undefined>) => {
  const _url = new URL(url);
  if (params) {
    Object.entries(params).forEach(([key, value]) => {
      // Note: this will skip empty strings and '0's, the API will not ignore a key with empty value and result will be an empty list.
      if (value) {
        _url.searchParams.append(key, String(value).toLowerCase());
      }
    });
  }
  return _url.toString();
};
