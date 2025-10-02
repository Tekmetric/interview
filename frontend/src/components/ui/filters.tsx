'use client'

import * as React from 'react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import { X, ListFilter, Layers, Flag, Users, Clock } from 'lucide-react'
import { cn } from '@/lib/utils'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { nanoid } from 'nanoid'

// Filter Types
export enum FilterType {
  STATUS = 'Status',
  PRIORITY = 'Priority',
  TECHNICIAN = 'Technician',
  OVERDUE = 'Overdue',
}

export enum FilterOperator {
  IS = 'is',
  IS_NOT = 'is not',
}

export interface FilterOption {
  name: string
  icon?: React.ReactNode
  label?: string
}

export interface Filter {
  id: string
  type: FilterType
  operator: FilterOperator
  value: string[]
}

// Animation wrapper for smooth height transitions
export function AnimateChangeInHeight({ children }: { children: React.ReactNode }) {
  return (
    <div className='overflow-hidden transition-all duration-200 ease-in-out'>
      {children}
    </div>
  )
}

// Get filter icon based on type
const getFilterIcon = (type: FilterType) => {
  switch (type) {
    case FilterType.STATUS:
      return <Layers className='size-3.5 shrink-0' />
    case FilterType.PRIORITY:
      return <Flag className='size-3.5 shrink-0' />
    case FilterType.TECHNICIAN:
      return <Users className='size-3.5 shrink-0' />
    case FilterType.OVERDUE:
      return <Clock className='size-3.5 shrink-0' />
    default:
      return null
  }
}

// Get filter color classes based on type
const getFilterColorClasses = (type: FilterType) => {
  switch (type) {
    case FilterType.STATUS:
      return 'bg-blue-500/10 text-blue-700 dark:text-blue-400 hover:bg-blue-500/20 border-blue-200 dark:border-blue-800'
    case FilterType.PRIORITY:
      return 'bg-orange-500/10 text-orange-700 dark:text-orange-400 hover:bg-orange-500/20 border-orange-200 dark:border-orange-800'
    case FilterType.TECHNICIAN:
      return 'bg-purple-500/10 text-purple-700 dark:text-purple-400 hover:bg-purple-500/20 border-purple-200 dark:border-purple-800'
    case FilterType.OVERDUE:
      return 'bg-amber-500/10 text-amber-700 dark:text-amber-400 hover:bg-amber-500/20 border-amber-200 dark:border-amber-800'
    default:
      return 'bg-secondary text-secondary-foreground hover:bg-secondary/80'
  }
}

// Individual filter badge component
function FilterBadge({
  filter,
  onRemove,
  onEdit,
}: {
  filter: Filter
  onRemove: () => void
  onEdit: () => void
}) {
  const displayValue = filter.value.join(', ')
  const icon = getFilterIcon(filter.type)
  const colorClasses = getFilterColorClasses(filter.type)

  return (
    <Badge
      variant='secondary'
      className={cn(
        'flex h-7 items-center gap-1.5 rounded-md border px-2.5 text-sm font-medium transition-all',
        colorClasses,
      )}
    >
      {icon}
      <span className='font-semibold'>{filter.type}:</span>
      <button
        onClick={onEdit}
        className='rounded-sm transition-colors hover:underline'
        aria-label='Edit filter'
      >
        {displayValue}
      </button>
      <button
        onClick={onRemove}
        className='ml-1 rounded-sm p-0.5 transition-colors hover:bg-black/10 dark:hover:bg-white/10'
        aria-label='Remove filter'
      >
        <X className='h-3.5 w-3.5' />
      </button>
    </Badge>
  )
}

// Main filters component
interface FiltersProps {
  filters: Filter[]
  setFilters: React.Dispatch<React.SetStateAction<Filter[]>>
  onEditFilter?: (filterType: FilterType) => void
}

export function Filters({ filters, setFilters, onEditFilter }: FiltersProps) {
  const removeFilter = (id: string) => {
    setFilters((prev) => prev.filter((f) => f.id !== id))
  }

  return (
    <>
      {filters.map((filter) => (
        <FilterBadge
          key={filter.id}
          filter={filter}
          onRemove={() => removeFilter(filter.id)}
          onEdit={() => onEditFilter?.(filter.type)}
        />
      ))}
    </>
  )
}

// Filter popover with command menu
interface FilterPopoverProps {
  filters: Filter[]
  setFilters: React.Dispatch<React.SetStateAction<Filter[]>>
  filterViewOptions: FilterOption[][]
  filterViewToFilterOptions: Record<FilterType, FilterOption[]>
  editFilterType?: FilterType | null
  onEditComplete?: () => void
}

export function FilterPopover({
  filters,
  setFilters,
  filterViewOptions,
  filterViewToFilterOptions,
  editFilterType,
  onEditComplete,
}: FilterPopoverProps) {
  const [open, setOpen] = React.useState(false)
  const [selectedView, setSelectedView] = React.useState<FilterType | null>(null)
  const [commandInput, setCommandInput] = React.useState('')
  const commandInputRef = React.useRef<React.ElementRef<typeof CommandInput>>(null)

  React.useEffect(() => {
    if (editFilterType) {
      setSelectedView(editFilterType)
      setOpen(true)
      onEditComplete?.()
    }
  }, [editFilterType, onEditComplete])

  const toggleValue = (value: string) => {
    if (!selectedView) return

    const currentFilter = filters.find((f) => f.type === selectedView)
    const currentValues = currentFilter?.value || []
    const newValues = currentValues.includes(value)
      ? currentValues.filter((v) => v !== value)
      : [...currentValues, value]

    if (newValues.length === 0) {
      setFilters((prev) => prev.filter((f) => f.type !== selectedView))
    } else if (currentFilter) {
      setFilters((prev) =>
        prev.map((f) => (f.type === selectedView ? { ...f, value: newValues } : f)),
      )
    } else {
      setFilters((prev) => [
        ...prev,
        {
          id: nanoid(),
          type: selectedView,
          operator: FilterOperator.IS,
          value: newValues,
        },
      ])
    }
  }

  const resetToCategories = () => {
    setSelectedView(null)
    setCommandInput('')
  }

  return (
    <Popover
      open={open}
      onOpenChange={(open) => {
        setOpen(open)
        if (!open) {
          setTimeout(() => {
            resetToCategories()
          }, 200)
        }
      }}
    >
      <PopoverTrigger asChild>
        <Button
          variant='ghost'
          role='combobox'
          aria-expanded={open}
          size='sm'
          className={cn(
            'group flex h-6 items-center gap-1.5 rounded-sm text-sm transition',
            filters.length > 0 && 'bg-primary/10 hover:bg-primary/20 w-6',
          )}
        >
          <ListFilter
            className={cn(
              'size-4 shrink-0 transition-all',
              filters.length > 0
                ? 'text-primary'
                : 'text-muted-foreground group-hover:text-primary',
            )}
          />
          {!filters.length && 'Filter'}
        </Button>
      </PopoverTrigger>
      <PopoverContent className='w-[200px] p-0'>
        <AnimateChangeInHeight>
          <Command>
            <CommandInput
              placeholder={selectedView ? selectedView : 'Filter...'}
              className='h-9'
              value={commandInput}
              onInputCapture={(e) => {
                setCommandInput(e.currentTarget.value)
              }}
              ref={commandInputRef}
            />
            <CommandList>
              <CommandEmpty>No results found.</CommandEmpty>
              {selectedView ? (
                <CommandGroup>
                  {filterViewToFilterOptions[selectedView].map((filter: FilterOption) => {
                    const currentFilter = filters.find((f) => f.type === selectedView)
                    const isSelected = currentFilter?.value.includes(filter.name) || false
                    return (
                      <CommandItem
                        className='flex items-center gap-2.5 px-3 py-2'
                        key={filter.name}
                        value={filter.name}
                        onSelect={() => toggleValue(filter.name)}
                      >
                        <Checkbox
                          checked={isSelected}
                          className='pointer-events-none size-4 shrink-0'
                        />
                        <span className='flex shrink-0'>{filter.icon}</span>
                        <span className='text-foreground text-sm font-medium'>
                          {filter.name}
                        </span>
                        {filter.label && (
                          <span className='text-muted-foreground ml-auto text-xs'>
                            {filter.label}
                          </span>
                        )}
                      </CommandItem>
                    )
                  })}
                </CommandGroup>
              ) : (
                filterViewOptions.map((group: FilterOption[], index: number) => (
                  <React.Fragment key={index}>
                    <CommandGroup>
                      {group.map((filter: FilterOption) => (
                        <CommandItem
                          className='flex items-center gap-2.5 px-3 py-2'
                          key={filter.name}
                          value={filter.name}
                          onSelect={(currentValue) => {
                            setSelectedView(currentValue as FilterType)
                            setCommandInput('')
                            commandInputRef.current?.focus()
                          }}
                        >
                          <span className='flex shrink-0'>{filter.icon}</span>
                          <span className='text-foreground text-sm font-medium'>
                            {filter.name}
                          </span>
                        </CommandItem>
                      ))}
                    </CommandGroup>
                  </React.Fragment>
                ))
              )}
            </CommandList>
          </Command>
        </AnimateChangeInHeight>
      </PopoverContent>
    </Popover>
  )
}
