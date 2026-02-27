import { cn } from '@/lib/classnames/cn'

type KpiCardProps = {
  title: string
  value: string
  icon: React.ReactNode
  iconClass: string
  valueClass?: string
}

export function KpiCard({ title, value, icon, iconClass, valueClass }: KpiCardProps) {
  return (
    <div className="tm-card flex flex-col gap-4 p-6">
      <div className="flex items-center justify-between">
        <p className="text-sm font-medium text-slate-600">{title}</p>
        <div className={cn('flex h-10 w-10 items-center justify-center rounded-xl', iconClass)}>
          {icon}
        </div>
      </div>
      <p className={cn('text-3xl font-bold tracking-tight', valueClass ?? 'text-foreground')}>
        {value}
      </p>
    </div>
  )
}
