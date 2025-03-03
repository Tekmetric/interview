export const API_ENDPOINTS = {
  getHistoricalData: (symbol: string) =>
    `https://api.tiingo.com/tiingo/daily/${symbol}/prices`,
  getSymbols: "https://finnhub.io/api/v1/search",
  getSymbolInsight: (symbol: string) =>
    `/api/openai/get-symbol-insight?${symbol}`,
  getAggregatedInsight: (symbols: string) =>
    `/api/openai/get-aggregated-insight?${symbols}`,
};
