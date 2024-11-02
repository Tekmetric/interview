'use client'

import { Button } from '@tekmetric/ui/button'
import { Icon } from '@tekmetric/ui/icon'

import { useResolveQuestion } from './services/use-resolve-question/use-resolve-question'

interface ResolveButtonProps {
  questionId: string
}

export const ResolveButton = ({
  questionId
}: ResolveButtonProps): JSX.Element => {
  const { resolveQuestion, loading } = useResolveQuestion(questionId)

  return (
    <Button
      variant='secondary'
      size='small'
      disabled={loading}
      onClick={resolveQuestion}
    >
      <Icon icon='check' /> Resolve
    </Button>
  )
}
