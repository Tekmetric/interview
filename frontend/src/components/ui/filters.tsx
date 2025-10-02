'use client'

import * as React from 'react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { X, ListFilter } from 'lucide-react'
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

// Individual filter badge component
function FilterBadge({ filter, onRemove }: { filter: Filter; onRemove: () => void }) {
  const displayValue = filter.value.join(', ')

  return (
    <Badge
      variant='secondary'
      className='hover:bg-secondary/80 flex h-6 items-center gap-1 rounded-sm px-2 text-xs transition-all'
    >
      <span className='text-muted-foreground'>{filter.type}:</span>
      <span className='font-medium'>{displayValue}</span>
      <button
        onClick={onRemove}
        className='hover:bg-muted ml-1 rounded-sm'
        aria-label='Remove filter'
      >
        <X className='h-3 w-3' />
      </button>
    </Badge>
  )
}

// Main filters component
interface FiltersProps {
  filters: Filter[]
  setFilters: React.Dispatch<React.SetStateAction<Filter[]>>
}

export function Filters({ filters, setFilters }: FiltersProps) {
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
}

export function FilterPopover({
  filters,
  setFilters,
  filterViewOptions,
  filterViewToFilterOptions,
}: FilterPopoverProps) {
  const [open, setOpen] = React.useState(false)
  const [selectedView, setSelectedView] = React.useState<FilterType | null>(null)
  const [commandInput, setCommandInput] = React.useState('')
  const commandInputRef = React.useRef<React.ElementRef<typeof CommandInput>>(null)

  return (
    <Popover
      open={open}
      onOpenChange={(open) => {
        setOpen(open)
        if (!open) {
          setTimeout(() => {
            setSelectedView(null)
            setCommandInput('')
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
            'group flex h-6 items-center gap-1.5 rounded-sm text-xs transition',
            filters.length > 0 && 'w-6',
          )}
        >
          <ListFilter className='size-3 shrink-0 transition-all text-muted-foreground group-hover:text-primary' />
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
                  {filterViewToFilterOptions[selectedView].map((filter: FilterOption) => (
                    <CommandItem
                      className='group text-muted-foreground flex items-center gap-2'
                      key={filter.name}
                      value={filter.name}
                      onSelect={(currentValue) => {
                        setFilters((prev) => [
                          ...prev,
                          {
                            id: nanoid(),
                            type: selectedView,
                            operator: FilterOperator.IS,
                            value: [currentValue],
                          },
                        ])
                        setTimeout(() => {
                          setSelectedView(null)
                          setCommandInput('')
                        }, 200)
                        setOpen(false)
                      }}
                    >
                      {filter.icon}
                      <span className='text-accent-foreground'>{filter.name}</span>
                      {filter.label && (
                        <span className='text-muted-foreground ml-auto text-xs'>
                          {filter.label}
                        </span>
                      )}
                    </CommandItem>
                  ))}
                </CommandGroup>
              ) : (
                filterViewOptions.map((group: FilterOption[], index: number) => (
                  <React.Fragment key={index}>
                    <CommandGroup>
                      {group.map((filter: FilterOption) => (
                        <CommandItem
                          className='group text-muted-foreground flex items-center gap-2'
                          key={filter.name}
                          value={filter.name}
                          onSelect={(currentValue) => {
                            setSelectedView(currentValue as FilterType)
                            setCommandInput('')
                            commandInputRef.current?.focus()
                          }}
                        >
                          {filter.icon}
                          <span className='text-accent-foreground'>{filter.name}</span>
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
