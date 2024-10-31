'use client'

import type { ReactNode } from 'react'
import {
  type FieldInputProps,
  type FieldMetaState,
  Field as FormField
} from 'react-final-form'

import {
  FieldClassErrorNames,
  FieldClassLabelNames,
  FieldClassNames
} from './styles'

interface FieldProps<TValue = unknown> {
  name: string
  label: string
  children: (props: {
    input: FieldInputProps<TValue, HTMLInputElement>
    meta: FieldMetaState<TValue>
  }) => ReactNode
}

export const Field = <TValue, TOutputValue extends TValue = TValue>({
  name,
  label,
  children
}: FieldProps<TValue>): JSX.Element => (
  <FormField<TOutputValue, HTMLElement, TValue> name={name}>
    {({ input, meta }) => {
      const hasError = Boolean(meta.error && meta.touched)

      return (
        <fieldset className={FieldClassNames}>
          <label htmlFor={name} className={FieldClassLabelNames}>
            {label}
          </label>

          {children({ input, meta })}

          {hasError && (
            <span className={FieldClassErrorNames}>{meta.error}</span>
          )}
        </fieldset>
      )
    }}
  </FormField>
)
