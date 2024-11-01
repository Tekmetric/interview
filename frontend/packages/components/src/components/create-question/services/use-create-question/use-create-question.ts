'use client'

import { CreateQuestionDocument, useMutation } from '@tekmetric/graphql'
import { useRouter } from 'next/navigation'
import { useCallback } from 'react'

import type { CreateQuestionFormFields } from '../../types'

export const useCreateQuestion = (): {
  handleSubmit: (values: CreateQuestionFormFields) => Promise<void>
  hasGlobalError: boolean
} => {
  const router = useRouter()
  const [createQuestion, { error: globalError }] = useMutation(
    CreateQuestionDocument
  )

  const hasGlobalError = Boolean(globalError)

  const handleSubmit = useCallback(
    async ({ title, description }: CreateQuestionFormFields) => {
      const result = await createQuestion({
        variables: { input: { title, description } }
      })

      if (result.data?.createQuestion.id && !result.errors?.length) {
        router.push(`/dashboard`)
      }
    },
    [createQuestion, router]
  )

  return { handleSubmit, hasGlobalError }
}
