'use client'

import {
  GetQuestionDocument,
  GetQuestionsDocument,
  ResolveQuestionDocument,
  useMutation
} from '@tekmetric/graphql'
import { useCallback } from 'react'

import { useToastContainer } from '../../../../../../services/use-toast-container/use-toast-container'

export const useResolveQuestion = (
  questionId: string
): {
  resolveQuestion: () => Promise<void>
  hasGlobalError: boolean
  loading: boolean
} => {
  const { notify } = useToastContainer()
  const [resolveQuestion, { error: globalError, loading }] = useMutation(
    ResolveQuestionDocument,
    { refetchQueries: [GetQuestionsDocument, GetQuestionDocument] }
  )
  const hasGlobalError = Boolean(globalError)

  const handleSubmit = useCallback(async () => {
    const data = await resolveQuestion({ variables: { id: questionId } })

    if (!data.errors?.length) {
      notify({ message: 'Question resolved successfully' })
    } else {
      notify({ type: 'error', message: 'Failed to resolve question' })
    }
  }, [notify, questionId, resolveQuestion])

  return { resolveQuestion: handleSubmit, hasGlobalError, loading }
}
