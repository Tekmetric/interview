export const API_ENDPOINTS = {
  getHistoricalData: (symbol: string) =>
    `https://api.tiingo.com/tiingo/daily/${symbol}/prices`,
  getSymbols: "https://finnhub.io/api/v1/search",
};
