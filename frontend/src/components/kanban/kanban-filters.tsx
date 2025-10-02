import { useEffect, useState } from 'react'
import { Input } from '@/components/ui/input'
import { Users, Flag, Layers, Search, Clock } from 'lucide-react'

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
      icon: <Layers className='size-4 shrink-0 text-blue-600 dark:text-blue-400' />,
    },
    {
      name: FilterType.PRIORITY,
      icon: <Flag className='size-4 shrink-0 text-orange-600 dark:text-orange-400' />,
    },
    {
      name: FilterType.TECHNICIAN,
      icon: <Users className='size-4 shrink-0 text-purple-600 dark:text-purple-400' />,
    },
    {
      name: FilterType.OVERDUE,
      icon: <Clock className='size-4 shrink-0 text-amber-600 dark:text-amber-400' />,
    },
  ],
]

const statusOptions: FilterOption[] = [
  { name: 'New', icon: <div className='size-2.5 rounded-full bg-blue-500' /> },
  {
    name: 'Awaiting Approval',
    icon: <div className='size-2.5 rounded-full bg-amber-500' />,
  },
  { name: 'In Progress', icon: <div className='size-2.5 rounded-full bg-indigo-500' /> },
  {
    name: 'Waiting Parts',
    icon: <div className='size-2.5 rounded-full bg-orange-500' />,
  },
  { name: 'Completed', icon: <div className='size-2.5 rounded-full bg-green-500' /> },
]

const priorityOptions: FilterOption[] = [
  { name: 'High', icon: <Flag className='size-3.5 text-red-500' /> },
  { name: 'Normal', icon: <Flag className='size-3.5 text-gray-400' /> },
]

const overdueOptions: FilterOption[] = [
  { name: 'Overdue', icon: <Clock className='size-3.5 text-amber-600' /> },
]

export function KanbanFilters({
  filters,
  onFiltersChange,
  searchQuery,
  onSearchChange,
  technicians,
}: KanbanFiltersProps) {
  const [localFilters, setLocalFilters] = useState<Filter[]>(filters)
  const [editingFilterType, setEditingFilterType] = useState<FilterType | null>(null)

  // Sync local filters with parent
  useEffect(() => {
    onFiltersChange(localFilters)
  }, [localFilters, onFiltersChange])

  const technicianOptions: FilterOption[] = [
    { name: 'Unassigned', icon: <Users className='size-3.5 text-gray-400' /> },
    ...technicians
      .filter((t) => t.active)
      .map((tech) => ({
        name: tech.name,
        icon: <Users className='size-3.5 text-purple-500' />,
      })),
  ]

  const filterViewToFilterOptions: Record<FilterType, FilterOption[]> = {
    [FilterType.STATUS]: statusOptions,
    [FilterType.PRIORITY]: priorityOptions,
    [FilterType.TECHNICIAN]: technicianOptions,
    [FilterType.OVERDUE]: overdueOptions,
  }

  return (
    <div className='flex flex-wrap items-center gap-2'>
      <div className='relative min-w-[240px] flex-1'>
        <Search className='absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-gray-400' />
        <Input
          placeholder={FILTER_LABELS.SEARCH_PLACEHOLDER}
          value={searchQuery}
          onChange={(e) => onSearchChange(e.target.value)}
          className='h-8 pl-9'
        />
      </div>

      <Filters
        filters={localFilters}
        setFilters={setLocalFilters}
        onEditFilter={(filterType) => setEditingFilterType(filterType)}
      />

      <FilterPopover
        filters={localFilters}
        setFilters={setLocalFilters}
        filterViewOptions={filterViewOptions}
        filterViewToFilterOptions={filterViewToFilterOptions}
        editFilterType={editingFilterType}
        onEditComplete={() => setEditingFilterType(null)}
      />
    </div>
  )
}
