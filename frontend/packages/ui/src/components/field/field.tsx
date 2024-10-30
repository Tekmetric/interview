import type { ReactNode } from 'react'

import { FieldClassLabelNames, FieldClassNames } from './styles'

interface FieldProps {
  name: string
  label: string
  children: ({ name }: { name: string }) => ReactNode
}

export const Field = ({ name, label, children }: FieldProps): JSX.Element => (
  <fieldset className={FieldClassNames}>
    <label htmlFor={name} className={FieldClassLabelNames}>
      {label}
    </label>

    {children({ name })}
  </fieldset>
)
