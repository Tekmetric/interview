import { Wrench } from 'lucide-react'
import { cn } from '@/lib/classnames/cn'

type BrandMarkProps = {
  showText?: boolean
  className?: string
  textClassName?: string
}

export function BrandMark({
  showText = true,
  className = 'flex items-center gap-3',
  textClassName = 'text-lg font-bold tracking-tight text-white',
}: BrandMarkProps) {
  return (
    <div className={cn('flex items-center gap-3', className)}>
      <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand">
        <Wrench className="h-5 w-5 text-white" strokeWidth={2} />
      </div>
      {showText && (
        <span className={cn('text-lg font-bold tracking-tight text-white', textClassName)}>
          TekShop
        </span>
      )}
    </div>
  )
}
