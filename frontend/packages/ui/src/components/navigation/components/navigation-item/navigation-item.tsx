import type { PropsWithChildren } from 'react'

import { NavigationItemClassNames } from './styles'

export interface NavigationItemProps {
  active?: boolean
}

export const NavigationItem = ({
  active,
  children
}: PropsWithChildren<NavigationItemProps>): JSX.Element => (
  <li className={NavigationItemClassNames({ active })}>{children}</li>
)
