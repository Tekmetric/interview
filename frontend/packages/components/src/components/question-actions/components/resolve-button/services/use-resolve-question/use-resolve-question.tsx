'use client'

import {
  GetQuestionDocument,
  GetQuestionsDocument,
  ResolveQuestionDocument,
  useMutation
} from '@tekmetric/graphql'
import { useCallback } from 'react'

export const useResolveQuestion = (
  questionId: string
): {
  resolveQuestion: () => Promise<void>
  hasGlobalError: boolean
  loading: boolean
} => {
  const [resolveQuestion, { error: globalError, loading }] = useMutation(
    ResolveQuestionDocument,
    { refetchQueries: [GetQuestionsDocument, GetQuestionDocument] }
  )
  const hasGlobalError = Boolean(globalError)

  const handleSubmit = useCallback(async () => {
    await resolveQuestion({ variables: { id: questionId } })
  }, [questionId, resolveQuestion])

  return { resolveQuestion: handleSubmit, hasGlobalError, loading }
}
