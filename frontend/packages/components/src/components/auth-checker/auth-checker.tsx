'use client'

import { usePathname, useRouter } from 'next/navigation'
import { useEffect } from 'react'

import { Routes } from '../../enums/routes'
import { useAuthStore } from '../../services/use-auth-store/use-auth-store'

export const AuthChecker = (): null => {
  const router = useRouter()
  const pathname = usePathname()
  const userId = useAuthStore((state) => state.userId)

  useEffect(() => {
    if (!userId) {
      router.push(Routes.Login)
    }
  }, [router, pathname, userId])

  return null
}
