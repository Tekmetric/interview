// Met Collection API — keyless and CORS-enabled, so it's called straight from
// the browser. Every request takes an AbortSignal so callers can cancel stale work.
const BASE = 'https://collectionapi.metmuseum.org/public/collection/v1';

async function getJson(url, signal) {
  const res = await fetch(url, { signal });
  if (!res.ok) throw new Error(`Request failed (${res.status})`);
  return res.json();
}

export async function fetchDepartments(signal) {
  const data = await getJson(`${BASE}/departments`, signal);
  return (data.departments ?? []).map((d) => ({
    id: d.departmentId,
    name: d.displayName,
  }));
}

// Search returns only matching object IDs plus a total; callers page through the
// IDs and fetch each object's detail lazily. hasImages drops empty records.
export async function searchObjects({ query, departmentId, signal }) {
  const q = query?.trim();
  if (!q) return { total: 0, ids: [] };
  const params = new URLSearchParams({ q, hasImages: 'true' });
  if (departmentId) params.set('departmentId', String(departmentId));
  const data = await getJson(`${BASE}/search?${params}`, signal);
  return { total: data.total ?? 0, ids: data.objectIDs ?? [] };
}

export function normalizeObject(raw) {
  return {
    id: raw.objectID,
    title: raw.title?.trim() || null,
    artist: raw.artistDisplayName?.trim() || null,
    date: raw.objectDate?.trim() || null,
    medium: raw.medium?.trim() || null,
    department: raw.department?.trim() || null,
    culture: raw.culture?.trim() || null,
    thumbnail: raw.primaryImageSmall || null,
    image: raw.primaryImage || raw.primaryImageSmall || null,
    isPublicDomain: Boolean(raw.isPublicDomain),
    url: raw.objectURL || null,
  };
}

export async function fetchObject(id, signal) {
  const data = await getJson(`${BASE}/objects/${id}`, signal);
  return normalizeObject(data);
}
