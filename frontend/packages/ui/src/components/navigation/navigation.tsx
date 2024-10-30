import { type PropsWithChildren, type ReactNode } from 'react'

import { NavigationButton } from './components/navigation-button/navigation-button'
import { NavigationItem } from './components/navigation-item/navigation-item'
import { NavigationClassNames, NavigationListClassNames } from './styles'

interface NavigationProps {
  actions?: ReactNode
}

export const Navigation = ({
  children,
  actions
}: PropsWithChildren<NavigationProps>): JSX.Element => {
  return (
    <div>
      <NavigationButton />

      <nav className={NavigationClassNames}>
        <ol className={NavigationListClassNames}>{children}</ol>

        {Boolean(actions) && <div>{actions}</div>}
      </nav>
    </div>
  )
}

Navigation.Item = NavigationItem
