import { memo } from 'react'
import type { FC, ReactNode, MouseEvent } from 'react'

import { cn } from '@/app/lib/utils'

interface NavLinkProps {
  href: string
  children: ReactNode
  className?: string
  onClick?: (e: MouseEvent<HTMLAnchorElement>) => void
}

export const NavLink: FC<NavLinkProps> = memo(
  ({ href, children, className, onClick }) => {
    return (
      <a
        href={href}
        data-testid={`nav-link-${href.replace('#', '').replace(' ', '-').toLowerCase()}`}
        className={cn(
          'text-foreground hover:text-primary transition-colors text-sm focus:outline-none focus:ring-2 focus:ring-primary rounded-md py-1 px-2',
          className
        )}
        onClick={onClick}
      >
        {children}
      </a>
    )
  }
)

NavLink.displayName = 'NavLink'
