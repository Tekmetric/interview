'use client'

import { Button } from '@tekmetric/ui/button'
import { Icon } from '@tekmetric/ui/icon'
import { Navigation as Nav } from '@tekmetric/ui/navigation'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

import { isMenuItemActive } from './services/is-menu-item-active/is-menu-item-active'

export const Navigation = (): JSX.Element => {
  const pathname = usePathname()

  return (
    <Nav
      pathname={pathname}
      actions={
        <Button size='small'>
          <Icon icon='logout' /> Logout
        </Button>
      }
    >
      <Nav.Item active={isMenuItemActive(pathname, '/dashboard')}>
        <Link href='/dashboard'>Pending Questions</Link>
      </Nav.Item>
      <Nav.Item active={isMenuItemActive(pathname, '/dashboard/completed')}>
        <Link href='/dashboard/completed'>Completed</Link>
      </Nav.Item>
    </Nav>
  )
}
