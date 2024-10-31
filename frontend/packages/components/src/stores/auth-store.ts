'use client'

import { type StoreApi, createStore } from 'zustand/vanilla'

import { AuthStorage } from '../services/auth-storage/auth-storage'

export interface AuthProfile {
  userId?: string | null
  loggedIn: boolean
}

export interface AuthActions {
  setSession: (props: { userId: string }) => void
  clearSession: () => void
}

export type AuthStore = AuthProfile & AuthActions

export const initAuthStore = (userId: string | null): AuthProfile => {
  return { userId, loggedIn: Boolean(userId) }
}

export const defaultInitState: AuthProfile = {
  userId: null,
  loggedIn: false
}

export const createAuthStore = (
  initState: AuthProfile = defaultInitState
): StoreApi<AuthStore> => {
  return createStore<AuthStore>()((set) => ({
    ...initState,
    setSession: ({ userId }) => {
      AuthStorage.setSession(userId)

      set(() => ({ userId, loggedIn: true }))
    },
    clearSession: () => {
      AuthStorage.removeSession()

      set(() => ({ userId: null, loggedIn: false }))
    }
  }))
}
