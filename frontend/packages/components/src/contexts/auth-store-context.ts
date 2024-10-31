'use client'

import { createContext } from 'react'

import { type createAuthStore } from '../stores/auth-store'

export type AuthStoreApi = ReturnType<typeof createAuthStore>
export const AuthStoreContext = createContext<AuthStoreApi | undefined>(
  undefined
)
