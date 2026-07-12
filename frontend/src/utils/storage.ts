// localStorage can throw (Safari private mode, disabled storage, quota).
// Persistence is a nice-to-have here, so failures degrade silently.

export function readStorage(key: string): string | null {
  try {
    return window.localStorage.getItem(key);
  } catch {
    return null;
  }
}

export function writeStorage(key: string, value: string): void {
  try {
    window.localStorage.setItem(key, value);
  } catch {
    // Ignore: the app works without persistence.
  }
}
