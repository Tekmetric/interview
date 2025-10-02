import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Badge } from '@/components/ui/badge'
import { Search, X } from 'lucide-react'
import type { Technician } from '@shared/types'
import { FILTER_LABELS, COMMON_LABELS } from '@shared/constants'
import { FilterPresetDropdown } from './filter-preset-dropdown'

type KanbanFiltersProps = {
  searchQuery: string
  onSearchChange: (query: string) => void
  priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
  onPriorityChange: (priority: 'ALL' | 'HIGH' | 'NORMAL') => void
  techFilter: string
  onTechChange: (techId: string) => void
  technicians: Technician[]
  onApplyPreset?: (filters: {
    searchQuery: string
    priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
    techFilter: string
  }) => void
}

export function KanbanFilters({
  searchQuery,
  onSearchChange,
  priorityFilter,
  onPriorityChange,
  techFilter,
  onTechChange,
  technicians,
  onApplyPreset,
}: KanbanFiltersProps) {
  const activeFiltersCount =
    (searchQuery ? 1 : 0) + (priorityFilter !== 'ALL' ? 1 : 0) + (techFilter !== 'ALL' ? 1 : 0)

  const clearAll = () => {
    onSearchChange('')
    onPriorityChange('ALL')
    onTechChange('ALL')
  }

  return (
    <div className='flex flex-col gap-3 rounded-lg bg-white p-4 shadow-sm'>
      <div className='flex items-center justify-between'>
        <div className='flex items-center gap-2'>
          <h3 className='text-sm font-semibold text-gray-700'>{FILTER_LABELS.TITLE}</h3>
          {activeFiltersCount > 0 && (
            <Badge variant='secondary' className='h-5 text-xs'>
              {activeFiltersCount}
            </Badge>
          )}
        </div>
        {activeFiltersCount > 0 && (
          <button
            onClick={clearAll}
            className='flex items-center gap-1 text-xs text-gray-500 hover:text-gray-700'
          >
            <X className='h-3 w-3' />
            {COMMON_LABELS.CLEAR_ALL}
          </button>
        )}
      </div>

      <div className='flex flex-col gap-3'>
        <div className='flex flex-wrap gap-3'>
          {/* Search */}
          <div className='relative flex-1 min-w-[240px]'>
            <Search className='absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400' />
            <Input
              placeholder={FILTER_LABELS.SEARCH_PLACEHOLDER}
              value={searchQuery}
              onChange={(e) => onSearchChange(e.target.value)}
              className='pl-9'
            />
          </div>

          {/* Priority Filter */}
          <Select value={priorityFilter} onValueChange={onPriorityChange}>
            <SelectTrigger className='w-[140px]'>
              <SelectValue placeholder={FILTER_LABELS.PRIORITY} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value='ALL'>{FILTER_LABELS.ALL_PRIORITY}</SelectItem>
              <SelectItem value='HIGH'>{FILTER_LABELS.HIGH_PRIORITY}</SelectItem>
              <SelectItem value='NORMAL'>{FILTER_LABELS.NORMAL_PRIORITY}</SelectItem>
            </SelectContent>
          </Select>

          {/* Tech Filter */}
          <Select value={techFilter} onValueChange={onTechChange}>
            <SelectTrigger className='w-[180px]'>
              <SelectValue placeholder={FILTER_LABELS.ASSIGNED_TECH} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value='ALL'>{FILTER_LABELS.ALL_TECHNICIANS}</SelectItem>
              <SelectItem value='UNASSIGNED'>{COMMON_LABELS.UNASSIGNED}</SelectItem>
              {technicians
                .filter((t) => t.active)
                .map((tech) => (
                  <SelectItem key={tech.id} value={tech.id}>
                    {tech.name}
                  </SelectItem>
                ))}
            </SelectContent>
          </Select>
        </div>

        {/* Filter Presets */}
        {onApplyPreset && (
          <FilterPresetDropdown
            currentFilters={{ searchQuery, priorityFilter, techFilter }}
            onApplyPreset={onApplyPreset}
          />
        )}
      </div>
    </div>
  )
}
