'use client'

import {
  CreateAnswerDocument,
  GetQuestionDocument,
  useMutation
} from '@tekmetric/graphql'
import { useCallback } from 'react'

import type { CreateAnswerFormFields } from '../../types'

export const useCreateAnswer = (
  questionId: string
): {
  createAnswer: (values: CreateAnswerFormFields) => Promise<void>
  hasGlobalError: boolean
} => {
  const [createAnswer, { error: globalError }] = useMutation(
    CreateAnswerDocument,
    {
      refetchQueries: [GetQuestionDocument]
    }
  )

  const hasGlobalError = Boolean(globalError)

  const handleSubmit = useCallback(
    async ({ description }: CreateAnswerFormFields) => {
      await createAnswer({
        variables: { input: { questionId, description } }
      })
    },
    [createAnswer, questionId]
  )

  return { createAnswer: handleSubmit, hasGlobalError }
}
