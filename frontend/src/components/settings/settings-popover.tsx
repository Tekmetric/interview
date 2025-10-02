import type { ReactNode } from 'react'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { ColumnVisibilitySettings } from './column-visibility-settings'

type SettingsPopoverProps = {
  children: ReactNode
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function SettingsPopover({ children, open, onOpenChange }: SettingsPopoverProps) {
  return (
    <Popover open={open} onOpenChange={onOpenChange}>
      <PopoverTrigger asChild>{children}</PopoverTrigger>
      <PopoverContent className='w-80' align='start'>
        <div className='space-y-4'>
          <div className='space-y-2'>
            <h4 className='text-sm font-medium'>Settings</h4>
            <p className='text-muted-foreground text-sm'>
              Customize your kanban board display preferences
            </p>
          </div>
          <ColumnVisibilitySettings />
        </div>
      </PopoverContent>
    </Popover>
  )
}
