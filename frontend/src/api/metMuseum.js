// Met Collection API — keyless and CORS-enabled, so it's called straight from
// the browser. Every request takes an AbortSignal so callers can cancel stale work.
const BASE = 'https://collectionapi.metmuseum.org/public/collection/v1';

// The API rate-limits under load; these statuses are worth a retry.
const RETRY_STATUS = new Set([429, 500, 502, 503, 504]);

function delay(ms, signal) {
  return new Promise((resolve, reject) => {
    const timer = setTimeout(resolve, ms);
    signal?.addEventListener(
      'abort',
      () => {
        clearTimeout(timer);
        reject(new DOMException('Aborted', 'AbortError'));
      },
      { once: true }
    );
  });
}

// Single retry/backoff policy for every call, so rate-limited requests get a
// couple of chances (400ms, 800ms) before failing instead of dropping silently.
async function getJson(url, signal, { retries = 2 } = {}) {
  for (let attempt = 0; ; attempt += 1) {
    const res = await fetch(url, { signal });
    if (res.ok) return res.json();
    if (attempt < retries && RETRY_STATUS.has(res.status)) {
      await delay(400 * 2 ** attempt, signal);
      continue;
    }
    throw new Error(`Request failed (${res.status})`);
  }
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
