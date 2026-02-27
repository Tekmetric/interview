'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { Car, LayoutDashboard, Users, Wrench } from 'lucide-react'
import { BrandMark } from '@/components/BrandMark/BrandMark'
import { cn } from '@/lib/classnames/cn'

const navItems = [
  {
    href: '/dashboard',
    label: 'Dashboard',
    icon: <LayoutDashboard className="h-5 w-5" />,
  },
  {
    href: '/customers',
    label: 'Customers',
    icon: <Users className="h-5 w-5" />,
  },
  {
    href: '/vehicles',
    label: 'Vehicles',
    icon: <Car className="h-5 w-5" />,
  },
  {
    href: '/repair-orders',
    label: 'Repair Orders',
    icon: <Wrench className="h-5 w-5" />,
  },
]

export function Sidebar() {
  const pathname = usePathname()

  return (
    <aside className="flex h-full w-60 shrink-0 flex-col bg-slate-950 text-slate-300">
      {/* Logo / wordmark */}
      <div className="flex items-center gap-3 px-5 py-6">
        <BrandMark />
      </div>

      {/* Divider */}
      <div className="mx-4 mb-4 border-t border-slate-800" />

      {/* Nav */}
      <nav className="flex-1 space-y-0.5 px-2">
        {navItems.map((item) => {
          const isActive = pathname === item.href || pathname.startsWith(item.href + '/')
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                isActive
                  ? 'border-l-2 border-brand bg-slate-800 pl-2.5 text-white [&_svg]:text-brand'
                  : 'border-l-2 border-transparent pl-2.5 hover:bg-slate-800/60 hover:text-slate-100',
              )}
            >
              {item.icon}
              {item.label}
            </Link>
          )
        })}
      </nav>

      {/* Footer */}
      <div className="px-4 py-5 text-xs text-slate-600">
        &copy; {new Date().getFullYear()} TekShop
      </div>
    </aside>
  )
}
