'use client'

import { type PropsWithChildren, useRef } from 'react'

import {
  type AuthStoreApi,
  AuthStoreContext
} from '../../contexts/auth-store-context'
import { createAuthStore, initAuthStore } from '../../stores/auth-store'

export interface AuthStoreProviderProps {
  session: string | null
}

export const AuthStoreProvider = ({
  children,
  session
}: PropsWithChildren<AuthStoreProviderProps>): JSX.Element => {
  const storeRef = useRef<AuthStoreApi>()

  if (!storeRef.current) {
    storeRef.current = createAuthStore(initAuthStore(session))
  }

  return (
    <AuthStoreContext.Provider value={storeRef.current}>
      {children}
    </AuthStoreContext.Provider>
  )
}
