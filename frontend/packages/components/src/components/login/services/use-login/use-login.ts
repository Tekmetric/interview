'use client'

import { LoginDocument, useMutation } from '@tekmetric/graphql'
import { useRouter } from 'next/navigation'
import { useCallback } from 'react'

import { useAuthStore } from '../../../../services/use-auth-store/use-auth-store'
import type { LoginFormFields } from '../../types'

export const useLogin = (): {
  handleSubmit: (values: LoginFormFields) => Promise<void>
  hasGlobalError: boolean
} => {
  const router = useRouter()
  const [login, { error: globalError }] = useMutation(LoginDocument)
  const { setSession } = useAuthStore((state) => state)
  const hasGlobalError = Boolean(globalError)

  const handleSubmit = useCallback(
    async ({ email, password }: LoginFormFields) => {
      const result = await login({ variables: { email, password } })

      if (result.data?.login?.userId && !result.errors?.length) {
        setSession({ userId: result.data.login.userId })

        router.push(`/dashboard`)
      }
    },
    [login, router, setSession]
  )

  return { handleSubmit, hasGlobalError }
}
