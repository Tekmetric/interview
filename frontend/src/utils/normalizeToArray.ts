// The API's batch endpoints (/character/1,2,3) return an array — unless the
// list contains a single id, in which case they return the bare object.
// Normalizing here keeps that quirk out of every consumer.
export function normalizeToArray<T>(value: T | T[]): T[] {
  return Array.isArray(value) ? value : [value];
}
