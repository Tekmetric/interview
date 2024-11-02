'use client'

import { useContext } from 'react'
import { useStore } from 'zustand'

import { AuthStoreContext } from '../../contexts/auth-store-context'
import { type AuthStore } from '../../stores/auth-store'

export const useAuthStore = <T>(selector: (store: AuthStore) => T): T => {
  const authStoreContext = useContext(AuthStoreContext)

  if (!authStoreContext) {
    throw new Error(`useAuthStore must be used within AuthStoreProvider`)
  }

  return useStore(authStoreContext, selector)
}
