import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { KanbanFilters } from '../filters'
import { FilterType } from '@/components/ui/filters'

describe('KanbanFilters', () => {
  const mockTechnicians = [
    { id: 'tech-1', name: 'John Doe', initials: 'JD', specialties: [], active: true },
    { id: 'tech-2', name: 'Jane Smith', initials: 'JS', specialties: [], active: true },
  ]

  it('should render the search input and filters', () => {
    render(
      <KanbanFilters
        filters={[]}
        onFiltersChange={vi.fn()}
        searchQuery=''
        onSearchChange={vi.fn()}
        technicians={mockTechnicians}
      />,
    )

    expect(
      screen.getByPlaceholderText('Search by RO ID, customer, vehicle...'),
    ).toBeInTheDocument()
    expect(screen.getByText('Filter')).toBeInTheDocument()
  })

  it('should call onSearchChange when the search input changes', () => {
    const onSearchChange = vi.fn()
    render(
      <KanbanFilters
        filters={[]}
        onFiltersChange={vi.fn()}
        searchQuery=''
        onSearchChange={onSearchChange}
        technicians={mockTechnicians}
      />,
    )

    const searchInput = screen.getByPlaceholderText(
      'Search by RO ID, customer, vehicle...',
    )
    fireEvent.change(searchInput, { target: { value: 'test' } })

    expect(onSearchChange).toHaveBeenCalledWith('test')
  })

  it('should call onFiltersChange when the filters are changed', () => {
    const onFiltersChange = vi.fn()
    render(
      <KanbanFilters
        filters={[]}
        onFiltersChange={onFiltersChange}
        searchQuery=''
        onSearchChange={vi.fn()}
        technicians={mockTechnicians}
      />,
    )

    // Simulate adding a filter
    const filterButton = screen.getByText('Filter')
    fireEvent.click(filterButton)

    const statusButton = screen.getByText('Status')
    fireEvent.click(statusButton)

    const createdButton = screen.getByText('Created')
    fireEvent.click(createdButton)

    expect(onFiltersChange).toHaveBeenCalledTimes(2)
    expect(onFiltersChange).toHaveBeenNthCalledWith(
      2,
      expect.arrayContaining([
        expect.objectContaining({
          type: FilterType.STATUS,
          value: ['Created'],
        }),
      ]),
    )
  })
})
