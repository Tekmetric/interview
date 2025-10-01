import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import type { ReactNode } from 'react'

interface KPICardProps {
  title: string
  value: number
  icon: ReactNode
  onClick?: () => void
  variant?: 'default' | 'primary' | 'warning' | 'info' | 'success'
}

export function KPICard({ title, value, icon, onClick, variant = 'default' }: KPICardProps) {
  const variantStyles = {
    default: 'bg-white border-gray-200',
    primary: 'bg-blue-50 border-blue-200',
    warning: 'bg-amber-50 border-amber-200',
    info: 'bg-purple-50 border-purple-200',
    success: 'bg-green-50 border-green-200',
  }

  const iconStyles = {
    default: 'text-gray-600',
    primary: 'text-blue-600',
    warning: 'text-amber-600',
    info: 'text-purple-600',
    success: 'text-green-600',
  }

  const valueStyles = {
    default: 'text-gray-900',
    primary: 'text-blue-900',
    warning: 'text-amber-900',
    info: 'text-purple-900',
    success: 'text-green-900',
  }

  return (
    <Card
      className={`${variantStyles[variant]} ${onClick ? 'cursor-pointer transition-shadow hover:shadow-md' : ''}`}
      onClick={onClick}
    >
      <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
        <CardTitle className='text-sm font-medium'>{title}</CardTitle>
        <div className={iconStyles[variant]}>{icon}</div>
      </CardHeader>
      <CardContent>
        <div className={`text-2xl font-bold ${valueStyles[variant]}`}>{value}</div>
      </CardContent>
    </Card>
  )
}
