import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { ColumnVisibilitySettings } from './column-visibility-settings'

type SettingsDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function SettingsDialog({ open, onOpenChange }: SettingsDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className='max-w-md'>
        <DialogHeader>
          <DialogTitle>Settings</DialogTitle>
          <DialogDescription>
            Customize your kanban board display preferences
          </DialogDescription>
        </DialogHeader>

        <div className='mt-4'>
          <ColumnVisibilitySettings />
        </div>
      </DialogContent>
    </Dialog>
  )
}
