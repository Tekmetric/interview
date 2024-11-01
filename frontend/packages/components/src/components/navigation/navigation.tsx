'use client'

import { Navigation as Nav } from '@tekmetric/ui/navigation'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

import { Routes } from '../../enums/routes'
import { LogoutButton } from './components/logout-button/logout-button'
import { isMenuItemActive } from './services/is-menu-item-active/is-menu-item-active'

export const Navigation = (): JSX.Element => {
  const pathname = usePathname()

  return (
    <Nav pathname={pathname} actions={<LogoutButton />}>
      <Nav.Item active={isMenuItemActive(pathname, Routes.Dashboard)}>
        <Link href={Routes.Dashboard}>Pending Questions</Link>
      </Nav.Item>
      <Nav.Item active={isMenuItemActive(pathname, Routes.Completed)}>
        <Link href={Routes.Completed}>Completed</Link>
      </Nav.Item>
      <Nav.Item active={isMenuItemActive(pathname, Routes.CreateQuestion)}>
        <Link href={Routes.CreateQuestion}>Create</Link>
      </Nav.Item>
    </Nav>
  )
}
