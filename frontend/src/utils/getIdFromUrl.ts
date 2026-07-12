// Detail payloads cross-link resources by URL, e.g.
// "https://rickandmortyapi.com/api/episode/12". The numeric id at the end is
// all we need to batch-fetch or route internally.
export function getIdFromUrl(url: string): number | null {
  const match = /\/(\d+)\/?$/.exec(url);
  return match ? Number(match[1]) : null;
}
