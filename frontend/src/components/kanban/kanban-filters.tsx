import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Users, Flag, Layers, Search } from 'lucide-react'
import * as React from 'react'
import {
  FilterType,
  FilterOption,
  Filter,
  Filters,
  FilterPopover,
} from '@/components/ui/filters'
import type { Technician } from '@shared/types'
import { FILTER_LABELS } from '@shared/constants'

type KanbanFiltersProps = {
  filters: Filter[]
  onFiltersChange: (filters: Filter[]) => void
  searchQuery: string
  onSearchChange: (query: string) => void
  technicians: Technician[]
}

// Filter view options (main categories)
export const filterViewOptions: FilterOption[][] = [
  [
    {
      name: FilterType.STATUS,
      icon: <Layers className='text-muted-foreground size-3 shrink-0' />,
    },
    {
      name: FilterType.PRIORITY,
      icon: <Flag className='text-muted-foreground size-3 shrink-0' />,
    },
    {
      name: FilterType.TECHNICIAN,
      icon: <Users className='text-muted-foreground size-3 shrink-0' />,
    },
  ],
]

// Status filter options
const statusOptions: FilterOption[] = [
  { name: 'New', icon: <div className='size-2 rounded-full bg-blue-500' /> },
  {
    name: 'Awaiting Approval',
    icon: <div className='size-2 rounded-full bg-amber-500' />,
  },
  { name: 'In Progress', icon: <div className='size-2 rounded-full bg-indigo-500' /> },
  { name: 'Waiting Parts', icon: <div className='size-2 rounded-full bg-orange-500' /> },
  { name: 'Completed', icon: <div className='size-2 rounded-full bg-green-500' /> },
]

// Priority filter options
const priorityOptions: FilterOption[] = [
  { name: 'High', icon: <Flag className='size-3 text-red-500' /> },
  { name: 'Normal', icon: <Flag className='size-3 text-gray-400' /> },
]

export function KanbanFilters({
  filters,
  onFiltersChange,
  searchQuery,
  onSearchChange,
  technicians,
}: KanbanFiltersProps) {
  const [localFilters, setLocalFilters] = React.useState<Filter[]>(filters)

  // Sync local filters with parent
  React.useEffect(() => {
    onFiltersChange(localFilters)
  }, [localFilters, onFiltersChange])

  // Generate technician options
  const technicianOptions: FilterOption[] = [
    { name: 'Unassigned', icon: <Users className='size-3 text-gray-400' /> },
    ...technicians
      .filter((t) => t.active)
      .map((tech) => ({
        name: tech.name,
        icon: <Users className='size-3 text-blue-500' />,
      })),
  ]

  // Map filter types to their options
  const filterViewToFilterOptions: Record<FilterType, FilterOption[]> = {
    [FilterType.STATUS]: statusOptions,
    [FilterType.PRIORITY]: priorityOptions,
    [FilterType.TECHNICIAN]: technicianOptions,
  }

  const hasActiveFilters = localFilters.filter((filter) => filter.value?.length > 0).length > 0

  return (
    <div className='flex flex-wrap items-center gap-2'>
      {/* Search Field */}
      <div className='relative min-w-[240px] flex-1'>
        <Search className='absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400' />
        <Input
          placeholder={FILTER_LABELS.SEARCH_PLACEHOLDER}
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          className='h-8 pl-9'
        />
      </div>

      {/* Filter Badges */}
      <Filters filters={localFilters} setFilters={setLocalFilters} />

      {/* Clear Button */}
      {hasActiveFilters && (
        <Button
          variant='outline'
          size='sm'
          className='group h-6 items-center rounded-sm text-xs transition'
          onClick={() => setLocalFilters([])}
        >
          Clear
        </Button>
      )}

      {/* Filter Popover */}
      <FilterPopover
        filters={localFilters}
        setFilters={setLocalFilters}
        filterViewOptions={filterViewOptions}
        filterViewToFilterOptions={filterViewToFilterOptions}
      />
    </div>
  )
}
