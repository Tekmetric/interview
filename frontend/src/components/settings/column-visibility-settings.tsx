import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { usePreferences } from '@/hooks/use-preferences'

export function ColumnVisibilitySettings() {
  const { preferences, updateColumnVisibility } = usePreferences()

  const columns = [
    { key: 'customerPhone', label: 'Customer Phone' },
    { key: 'vehicleDetails', label: 'Vehicle Details (Year/Make/Model)' },
    { key: 'assignedTech', label: 'Assigned Technician' },
    { key: 'dueTime', label: 'Due Time' },
    { key: 'services', label: 'Services' },
  ] as const

  return (
    <div className='space-y-4'>
      <div>
        <h3 className='text-sm font-semibold text-gray-900'>Card Display Options</h3>
        <p className='text-xs text-gray-600'>
          Choose which fields to show on kanban cards
        </p>
      </div>

      <div className='space-y-3'>
        {columns.map((column) => (
          <div key={column.key} className='flex items-center gap-3'>
            <Checkbox
              id={column.key}
              checked={preferences.columnVisibility[column.key]}
              onCheckedChange={(checked) =>
                updateColumnVisibility({ [column.key]: checked === true })
              }
            />
            <Label
              htmlFor={column.key}
              className='cursor-pointer text-sm font-normal text-gray-700'
            >
              {column.label}
            </Label>
          </div>
        ))}
      </div>
    </div>
  )
}
