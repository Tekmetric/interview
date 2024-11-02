'use client'

import { Button, type ButtonProps } from '@tekmetric/ui/button'
import { useRouter } from 'next/navigation'
import type { PropsWithChildren } from 'react'

import { Routes } from '../../../../enums/routes'

interface ViewQuestionButtonProps {
  questionId: string
  variant?: ButtonProps['variant']
}

export const ViewQuestionButton = ({
  questionId,
  variant = 'primary',
  children
}: PropsWithChildren<ViewQuestionButtonProps>): JSX.Element => {
  const router = useRouter()

  const handleClick = (): void => {
    router.push(`${Routes.Dashboard}/${questionId}`)
  }

  return (
    <Button size='small' variant={variant} onClick={handleClick}>
      {children}
    </Button>
  )
}
