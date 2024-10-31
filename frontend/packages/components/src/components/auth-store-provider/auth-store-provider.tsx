'use client'

import { type ReactNode, useRef } from 'react'

import {
  type AuthStoreApi,
  AuthStoreContext
} from '../../contexts/auth-store-context'
import { AuthStorage } from '../../services/auth-storage/auth-storage'
import { createAuthStore, initAuthStore } from '../../stores/auth-store'

export interface AuthStoreProviderProps {
  children: ReactNode
}

export const AuthStoreProvider = ({
  children
}: AuthStoreProviderProps): JSX.Element => {
  const storeRef = useRef<AuthStoreApi>()
  if (!storeRef.current) {
    storeRef.current = createAuthStore(initAuthStore(AuthStorage.getSession()))
  }

  return (
    <AuthStoreContext.Provider value={storeRef.current}>
      {children}
    </AuthStoreContext.Provider>
  )
}
