import { Navigation } from '@tekmetric/components/navigation'
import type { PropsWithChildren } from 'react'

import { LayoutClassNames, PageClassNames } from './styles'

const Layout = ({ children }: PropsWithChildren): JSX.Element => (
  <div className={LayoutClassNames}>
    <Navigation />

    <main className={PageClassNames}>{children}</main>
  </div>
)

export default Layout
