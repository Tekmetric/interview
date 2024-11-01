'use client'

import {
  CreateQuestionDocument,
  GetQuestionsDocument,
  useMutation
} from '@tekmetric/graphql'
import { useRouter } from 'next/navigation'
import { useCallback, useEffect } from 'react'

import { Routes } from '../../../../enums/routes'
import { useToastContainer } from '../../../../services/use-toast-container/use-toast-container'
import type { CreateQuestionFormFields } from '../../types'

export const useCreateQuestion = (): {
  createQuestion: (values: CreateQuestionFormFields) => Promise<void>
  hasGlobalError: boolean
} => {
  const { notify } = useToastContainer()
  const router = useRouter()
  const [createQuestion, { error: globalError }] = useMutation(
    CreateQuestionDocument,
    {
      refetchQueries: [GetQuestionsDocument]
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
    async ({ title, description }: CreateQuestionFormFields) => {
      const result = await createQuestion({
        variables: { input: { title, description } }
      })

      if (result.data?.createQuestion.id && !result.errors?.length) {
        router.push(Routes.Dashboard)
      }
    },
    [createQuestion, router]
  )

  return { createQuestion: handleSubmit, hasGlobalError }
}
