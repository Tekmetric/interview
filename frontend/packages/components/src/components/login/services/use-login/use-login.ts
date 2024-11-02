'use client'

import { LoginDocument, useMutation } from '@tekmetric/graphql'
import { useRouter } from 'next/navigation'
import { useCallback } from 'react'

import { Routes } from '../../../../enums/routes'
import { useAuthStore } from '../../../../services/use-auth-store/use-auth-store'
import type { LoginFormFields } from '../../types'

export const useLogin = (): {
  login: (values: LoginFormFields) => Promise<void>
  hasGlobalError: boolean
} => {
  const router = useRouter()
  const [login, { error: globalError }] = useMutation(LoginDocument)
  const { setSession } = useAuthStore((state) => state)
  const hasGlobalError = Boolean(globalError)

  const handleLogin = useCallback(
    async ({ email, password }: LoginFormFields) => {
      const result = await login({ variables: { email, password } })

      if (result.data?.login?.userId && !result.errors?.length) {
        setSession({ userId: result.data.login.userId })

        router.push(Routes.Dashboard)
      }
    },
    [login, router, setSession]
  )

  return { login: handleLogin, hasGlobalError }
}
