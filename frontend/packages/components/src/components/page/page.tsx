import type { PropsWithChildren } from 'react'

import { PageBody } from './components/page-body/page-body'
import { PageTitle } from './components/page-title/page-title'
import { PageClassNames } from './styles'

export const Page = ({ children }: PropsWithChildren): JSX.Element => (
  <div className={PageClassNames}>{children}</div>
)

Page.Title = PageTitle
Page.Body = PageBody
