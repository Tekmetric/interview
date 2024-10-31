import { getCookie, removeCookie, setCookie } from 'typescript-cookie'

const STORAGE_KEY = 'session'

const isClient = typeof window !== 'undefined'

export class AuthStorage {
  static setSession(token: string): void {
    if (!isClient) {
      return
    }

    setCookie(STORAGE_KEY, token)
  }

  static getSession(): string | null {
    if (!isClient) {
      return null
    }

    const result: string | undefined = getCookie(STORAGE_KEY)

    return result ?? null
  }

  static removeSession(): void {
    if (!isClient) {
      return
    }

    removeCookie(STORAGE_KEY)
  }
}
