'use client'

import type { FormApi, SubmissionErrors, ValidationErrors } from 'final-form'
import type { PropsWithChildren } from 'react'
import { Form as FinalForm } from 'react-final-form'

export interface FormProps<
  FormValues = object,
  InitialFormValues = Partial<FormValues>
> {
  onSubmit: (
    values: FormValues,
    form: FormApi<FormValues, InitialFormValues>,
    callback?: (errors?: SubmissionErrors) => void
  ) => SubmissionErrors | Promise<SubmissionErrors> | void
  validate?: (
    values: FormValues
  ) => ValidationErrors | Promise<ValidationErrors>
}

export const Form = <
  FormValues = Record<string, unknown>,
  InitialFormValues = Partial<FormValues>
>({
  children,
  validate,
  onSubmit
}: PropsWithChildren<
  FormProps<FormValues, InitialFormValues>
>): JSX.Element => {
  return (
    <FinalForm onSubmit={onSubmit} validate={validate}>
      {({ handleSubmit }) => (
        <form onSubmit={handleSubmit} noValidate className='tek-w-full'>
          {children}
        </form>
      )}
    </FinalForm>
  )
}
