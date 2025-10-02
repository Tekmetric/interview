import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { usePreferences } from '@/hooks/use-preferences'
import { toast } from 'sonner'
import { Star, Trash2, Play } from 'lucide-react'

type PresetManagerDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  onApplyPreset: (filters: {
    searchQuery: string
    priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
    techFilter: string
  }) => void
}

export function PresetManagerDialog({
  open,
  onOpenChange,
  onApplyPreset,
}: PresetManagerDialogProps) {
  const { preferences, deleteFilterPreset, setDefaultPreset, getPresetById } =
    usePreferences()

  const handleDelete = (presetId: string) => {
    const preset = getPresetById(presetId)
    deleteFilterPreset(presetId)
    toast.success(`Deleted preset "${preset?.name}"`)
  }

  const handleToggleDefault = (presetId: string) => {
    if (preferences.defaultFilterPreset === presetId) {
      setDefaultPreset(undefined)
      toast.success('Default preset cleared')
    } else {
      const preset = getPresetById(presetId)
      setDefaultPreset(presetId)
      toast.success(`Set "${preset?.name}" as default`)
    }
  }

  const handleApply = (presetId: string) => {
    const preset = getPresetById(presetId)
    if (preset) {
      onApplyPreset({
        searchQuery: preset.searchQuery,
        priorityFilter: preset.priorityFilter,
        techFilter: preset.techFilter,
      })
      onOpenChange(false)
      toast.success(`Applied preset "${preset.name}"`)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className='max-w-2xl'>
        <DialogHeader>
          <DialogTitle>Manage Filter Presets</DialogTitle>
          <DialogDescription>
            Apply, edit, or delete your saved filter presets
          </DialogDescription>
        </DialogHeader>

        <div className='space-y-2'>
          {preferences.savedFilters.length === 0 ? (
            <div className='py-8 text-center text-sm text-gray-500'>
              No saved presets. Save your current filters to create a preset.
            </div>
          ) : (
            preferences.savedFilters.map((preset) => {
              const isDefault = preferences.defaultFilterPreset === preset.id
              return (
                <div
                  key={preset.id}
                  className='flex items-start gap-3 rounded-lg border border-gray-200 bg-white p-4'
                >
                  <button
                    onClick={() => handleToggleDefault(preset.id)}
                    className='mt-1 text-gray-400 transition-colors hover:text-amber-400'
                    title={isDefault ? 'Remove as default' : 'Set as default'}
                  >
                    <Star
                      className={`h-5 w-5 ${isDefault ? 'fill-amber-400 text-amber-400' : ''}`}
                    />
                  </button>

                  <div className='flex-1'>
                    <h4 className='font-semibold text-gray-900'>{preset.name}</h4>
                    <div className='mt-1 flex flex-wrap gap-2 text-xs text-gray-600'>
                      {preset.searchQuery && (
                        <span className='rounded bg-blue-100 px-2 py-0.5 text-blue-700'>
                          Search: &quot;{preset.searchQuery}&quot;
                        </span>
                      )}
                      {preset.priorityFilter !== 'ALL' && (
                        <span className='rounded bg-amber-100 px-2 py-0.5 text-amber-700'>
                          Priority: {preset.priorityFilter}
                        </span>
                      )}
                      {preset.techFilter !== 'ALL' && (
                        <span className='rounded bg-purple-100 px-2 py-0.5 text-purple-700'>
                          Tech:{' '}
                          {preset.techFilter === 'UNASSIGNED' ? 'Unassigned' : 'Assigned'}
                        </span>
                      )}
                    </div>
                    {isDefault && (
                      <span className='mt-2 inline-block text-xs text-amber-600'>
                        Applied on page load
                      </span>
                    )}
                  </div>

                  <div className='flex gap-1'>
                    <Button
                      variant='ghost'
                      size='sm'
                      onClick={() => handleApply(preset.id)}
                      title='Apply this preset'
                    >
                      <Play className='h-4 w-4' />
                    </Button>
                    <Button
                      variant='ghost'
                      size='sm'
                      onClick={() => handleDelete(preset.id)}
                      className='text-red-600 hover:bg-red-50 hover:text-red-700'
                      title='Delete this preset'
                    >
                      <Trash2 className='h-4 w-4' />
                    </Button>
                  </div>
                </div>
              )
            })
          )}
        </div>
      </DialogContent>
    </Dialog>
  )
}
