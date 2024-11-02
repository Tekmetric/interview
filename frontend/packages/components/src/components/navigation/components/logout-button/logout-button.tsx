'use client'

import { Button } from '@tekmetric/ui/button'
import { Icon } from '@tekmetric/ui/icon'
import { useRouter } from 'next/navigation'
import { useCallback } from 'react'

import { useAuthStore } from '../../../../services/use-auth-store/use-auth-store'

export const LogoutButton = (): JSX.Element => {
  const router = useRouter()
  const { clearSession } = useAuthStore((state) => state)

  const handleClick = useCallback(() => {
    clearSession()

    router.push('/')
  }, [clearSession, router])

  return (
    <Button size='small' onClick={handleClick}>
      <Icon icon='logout' /> Logout
    </Button>
  )
}
