import type { components } from '@/lib/api/contracts/generated/api.types'
import { cn } from '@/lib/classnames/cn'

type RepairOrderStatus = components['schemas']['RepairOrder']['status']

const statusConfig: Record<
  RepairOrderStatus,
  { label: string; className: string; dotClass: string }
> = {
  new: {
    label: 'New',
    className: 'bg-slate-100 text-slate-600',
    dotClass: 'bg-slate-400',
  },
  in_progress: {
    label: 'In Progress',
    className: 'bg-brand-100 text-brand-700',
    dotClass: 'bg-brand',
  },
  waiting_parts: {
    label: 'Waiting Parts',
    className: 'bg-amber-100 text-amber-800',
    dotClass: 'bg-amber-500',
  },
  completed: {
    label: 'Completed',
    className: 'bg-green-100 text-green-700',
    dotClass: 'bg-green-500',
  },
}

type Props = {
  status: RepairOrderStatus
}

export function StatusBadge({ status }: Props) {
  const config = statusConfig[status]
  return (
    <span
      className={cn(
        'inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-semibold',
        config.className,
      )}
    >
      <span className={cn('h-1.5 w-1.5 rounded-full', config.dotClass)} />
      {config.label}
    </span>
  )
}
