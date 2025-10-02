import { useState } from 'react'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { usePreferences } from '@/hooks/use-preferences'
import { toast } from 'sonner'

type SavePresetDialogProps = {
  open: boolean
  onOpenChange: (open: boolean) => void
  currentFilters: {
    searchQuery: string
    priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
    techFilter: string
  }
}

export function SavePresetDialog({
  open,
  onOpenChange,
  currentFilters,
}: SavePresetDialogProps) {
  const { preferences, saveFilterPreset } = usePreferences()
  const [name, setName] = useState('')
  const [error, setError] = useState('')

  const handleSave = () => {
    if (!name.trim()) {
      setError('Preset name is required')
      return
    }

    const nameExists = preferences.savedFilters.some(
      (p) => p.name.toLowerCase() === name.trim().toLowerCase(),
    )
    if (nameExists) {
      setError('A preset with this name already exists')
      return
    }

    saveFilterPreset({
      name: name.trim(),
      ...currentFilters,
    })

    toast.success('Filter preset saved successfully')

    setName('')
    setError('')
    onOpenChange(false)
  }

  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      setName('')
      setError('')
    }
    onOpenChange(newOpen)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className='max-w-md'>
        <DialogHeader>
          <DialogTitle>Save Filter Preset</DialogTitle>
          <DialogDescription>
            Save your current filter combination for quick access later
          </DialogDescription>
        </DialogHeader>

        <div className='space-y-4'>
          <div className='space-y-2'>
            <Label htmlFor='preset-name'>Preset Name</Label>
            <Input
              id='preset-name'
              placeholder='e.g., High Priority Unassigned'
              value={name}
              onChange={(e) => {
                setName(e.target.value)
                setError('')
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleSave()
                }
              }}
            />
            {error && <p className='text-sm text-red-600'>{error}</p>}
          </div>

          <div className='rounded-lg bg-gray-50 p-3 text-sm'>
            <p className='font-medium text-gray-700'>Current Filters:</p>
            <ul className='mt-2 space-y-1 text-gray-600'>
              {currentFilters.searchQuery && (
                <li>• Search: &quot;{currentFilters.searchQuery}&quot;</li>
              )}
              {currentFilters.priorityFilter !== 'ALL' && (
                <li>• Priority: {currentFilters.priorityFilter}</li>
              )}
              {currentFilters.techFilter !== 'ALL' && (
                <li>
                  • Technician:{' '}
                  {currentFilters.techFilter === 'UNASSIGNED' ? 'Unassigned' : 'Assigned'}
                </li>
              )}
              {currentFilters.searchQuery === '' &&
                currentFilters.priorityFilter === 'ALL' &&
                currentFilters.techFilter === 'ALL' && (
                  <li className='text-gray-500'>No filters active</li>
                )}
            </ul>
          </div>
        </div>

        <DialogFooter>
          <Button variant='outline' onClick={() => handleOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={handleSave}>Save Preset</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
