import type { PropsWithChildren } from 'react'

import { PageBodyClassNames } from './styles'

export const PageBody = ({ children }: PropsWithChildren): JSX.Element => (
  <div className={PageBodyClassNames}>{children}</div>
)
