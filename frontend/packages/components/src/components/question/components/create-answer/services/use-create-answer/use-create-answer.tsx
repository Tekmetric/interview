'use client'

import {
  CreateAnswerDocument,
  GetQuestionDocument,
  useMutation
} from '@tekmetric/graphql'
import { useCallback, useEffect } from 'react'

import { useToastContainer } from '../../../../../../services/use-toast-container/use-toast-container'
import type { CreateAnswerFormFields } from '../../types'

export const useCreateAnswer = (
  questionId: string
): {
  createAnswer: (values: CreateAnswerFormFields) => Promise<void>
  hasGlobalError: boolean
} => {
  const { notify } = useToastContainer()
  const [createAnswer, { error: globalError }] = useMutation(
    CreateAnswerDocument,
    {
      refetchQueries: [GetQuestionDocument]
    }
  )

  const hasGlobalError = Boolean(globalError)

  useEffect(() => {
    if (hasGlobalError) {
      notify({
        type: 'error',
        message: 'Something went wrong. Please try again.'
      })
    }
  }, [hasGlobalError, notify])

  const handleSubmit = useCallback(
    async ({ description }: CreateAnswerFormFields) => {
      const data = await createAnswer({
        variables: { input: { questionId, description } }
      })

      if (data.errors?.length) {
        notify({ type: 'error', message: 'Failed to create the answer' })
      }
    },
    [createAnswer, notify, questionId]
  )

  return { createAnswer: handleSubmit, hasGlobalError }
}
