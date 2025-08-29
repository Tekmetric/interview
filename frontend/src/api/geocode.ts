export type GeoCity = {
  city?: string;
  locality?: string;
  principalSubdivision?: string;
  countryName?: string;
};

// Uses BigDataCloud free reverse-geocode endpoint (no API key required)
export async function reverseGeocodeCity(
  lat: number,
  lon: number,
): Promise<GeoCity | null> {
  const url = `https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=${lat}&longitude=${lon}&localityLanguage=en`;
  const res = await fetch(url, { headers: { accept: "application/json" } });
  if (!res.ok) return null;
  const data = (await res.json()) as GeoCity;
  return data ?? null;
}
