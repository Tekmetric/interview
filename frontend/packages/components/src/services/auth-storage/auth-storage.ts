const STORAGE_KEY = 'session'

export class AuthStorage {
  static setSession(token: string): void {
    localStorage.setItem(STORAGE_KEY, token)
  }

  static getSession(): string | null {
    return localStorage.getItem(STORAGE_KEY)
  }

  static removeSession(): void {
    localStorage.removeItem(STORAGE_KEY)
  }
}
