import { memo } from 'react'
import { cn } from '@/app/lib/utils'
import { NavLink } from './NavLink'

interface MobileMenuProps {
  isOpen: boolean
  navItems: ReadonlyArray<{ href: string; label: string }>
  onClose: () => void
  onNavClick: (e: React.MouseEvent<HTMLAnchorElement>) => void
}

export const MobileMenu: React.FC<MobileMenuProps> = memo(
  ({ isOpen, navItems, onClose, onNavClick }) => {
    return (
      <div
        className={cn(
          'md:hidden transition-all duration-300 ease-in-out overflow-hidden',
          isOpen ? 'max-h-64 opacity-100' : 'max-h-0 opacity-0'
        )}
        data-testid="mobile-menu"
      >
        <nav
          className="flex items-center justify-between py-4 px-2 bg-card/90 backdrop-blur-lg"
          aria-label="Mobile navigation"
          data-testid="mobile-navigation"
        >
          <div
            className="grid grid-cols-2 gap-4 w-full"
            data-testid="mobile-menu-items"
          >
            {navItems.map((item) => (
              <NavLink
                key={item.href}
                href={item.href}
                onClick={(e) => {
                  onNavClick(e)
                  onClose()
                }}
                className="text-center"
                data-testid={`nav-link-${item.label.toLowerCase().replace(' ', '-')}`}
              >
                {item.label}
              </NavLink>
            ))}
          </div>
        </nav>
      </div>
    )
  }
)

MobileMenu.displayName = 'MobileMenu'
