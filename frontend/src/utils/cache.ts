const cache: Record<string, any> = {};

export function setCache<T>(key: string, data: T): void {
  cache[key] = data;
}

export function getCache<T>(key: string): T | undefined {
  return cache[key];
}
