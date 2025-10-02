import { useState } from 'react'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Button } from '@/components/ui/button'
import { usePreferences } from '@/hooks/use-preferences'
import { SavePresetDialog } from './save-preset-dialog'
import { PresetManagerDialog } from './preset-manager-dialog'
import { Bookmark, Star } from 'lucide-react'

type FilterPresetDropdownProps = {
  currentFilters: {
    searchQuery: string
    priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
    techFilter: string
  }
  onApplyPreset: (filters: {
    searchQuery: string
    priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
    techFilter: string
  }) => void
}

export function FilterPresetDropdown({
  currentFilters,
  onApplyPreset,
}: FilterPresetDropdownProps) {
  const { preferences, getPresetById } = usePreferences()
  const [saveDialogOpen, setSaveDialogOpen] = useState(false)
  const [manageDialogOpen, setManageDialogOpen] = useState(false)

  const handleApplyPreset = (presetId: string) => {
    const preset = getPresetById(presetId)
    if (preset) {
      onApplyPreset({
        searchQuery: preset.searchQuery,
        priorityFilter: preset.priorityFilter,
        techFilter: preset.techFilter,
      })
    }
  }

  const hasPresets = preferences.savedFilters.length > 0

  return (
    <div className='flex items-center gap-2'>
      {hasPresets && (
        <Select onValueChange={handleApplyPreset}>
          <SelectTrigger className='w-[180px]'>
            <Bookmark className='mr-2 h-4 w-4' />
            <SelectValue placeholder='Apply Preset' />
          </SelectTrigger>
          <SelectContent>
            {preferences.savedFilters.map((preset) => (
              <SelectItem key={preset.id} value={preset.id}>
                <div className='flex items-center gap-2'>
                  {preferences.defaultFilterPreset === preset.id && (
                    <Star className='h-3 w-3 fill-amber-400 text-amber-400' />
                  )}
                  <span>{preset.name}</span>
                </div>
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      )}

      <Button
        variant='outline'
        size='sm'
        onClick={() => setSaveDialogOpen(true)}
        className='whitespace-nowrap'
      >
        <Bookmark className='mr-2 h-4 w-4' />
        Save Search
      </Button>

      {hasPresets && (
        <Button variant='ghost' size='sm' onClick={() => setManageDialogOpen(true)}>
          Manage
        </Button>
      )}

      <SavePresetDialog
        open={saveDialogOpen}
        onOpenChange={setSaveDialogOpen}
        currentFilters={currentFilters}
      />

      <PresetManagerDialog
        open={manageDialogOpen}
        onOpenChange={setManageDialogOpen}
        onApplyPreset={onApplyPreset}
      />
    </div>
  )
}
