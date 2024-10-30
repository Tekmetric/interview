'use client'

import {
  type PropsWithChildren,
  type ReactNode,
  useEffect,
  useState
} from 'react'

import { NavigationButton } from './components/navigation-button/navigation-button'
import { NavigationItem } from './components/navigation-item/navigation-item'
import { NavigationClassNames, NavigationListClassNames } from './styles'

interface NavigationProps {
  pathname?: string
  actions?: ReactNode
}

export const Navigation = ({
  children,
  actions,
  pathname
}: PropsWithChildren<NavigationProps>): JSX.Element => {
  const [isOpen, setIsOpen] = useState(false)

  const toggleMenu = (): void => {
    setIsOpen((state) => !state)
  }

  useEffect(() => {
    setIsOpen(false)
  }, [pathname])

  return (
    <div>
      <NavigationButton open={isOpen} onClick={toggleMenu} />

      <nav className={NavigationClassNames({ open: isOpen })}>
        <ol className={NavigationListClassNames}>{children}</ol>

        {Boolean(actions) && <div>{actions}</div>}
      </nav>
    </div>
  )
}

Navigation.Item = NavigationItem
