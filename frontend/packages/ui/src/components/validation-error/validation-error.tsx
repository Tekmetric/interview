import type { PropsWithChildren } from 'react'

import { ValidationErrorClassNames } from './styles'

export const ValidationError = ({
  children
}: PropsWithChildren): JSX.Element => (
  <div className={ValidationErrorClassNames}>{children}</div>
)
