import { render, screen, fireEvent } from '@testing-library/react'
import LaunchFilters from '../LaunchFilters'

describe('LaunchFilters', () => {
  const onFilterChange = jest.fn()
  const onSortChange = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('renders all filter elements', () => {
    render(
      <LaunchFilters
        onFilterChange={onFilterChange}
        onSortChange={onSortChange}
      />
    )
    expect(screen.getByTestId('launch-filters')).toBeInTheDocument()
    expect(screen.getByTestId('filters-title')).toBeInTheDocument()
    expect(screen.getByTestId('success-label')).toBeInTheDocument()
    expect(screen.getByTestId('success-select')).toBeInTheDocument()
    expect(screen.getByTestId('rocket-label')).toBeInTheDocument()
    expect(screen.getByTestId('rocket-select')).toBeInTheDocument()
    expect(screen.getByTestId('start-date-label')).toBeInTheDocument()
    expect(screen.getByTestId('start-date-input')).toBeInTheDocument()
    expect(screen.getByTestId('end-date-label')).toBeInTheDocument()
    expect(screen.getByTestId('end-date-input')).toBeInTheDocument()
    expect(screen.getByTestId('sort-label')).toBeInTheDocument()
    expect(screen.getByTestId('sort-select')).toBeInTheDocument()
  })

  it('calls onFilterChange when success filter changes', () => {
    render(
      <LaunchFilters
        onFilterChange={onFilterChange}
        onSortChange={onSortChange}
      />
    )
    fireEvent.change(screen.getByTestId('success-select'), {
      target: { name: 'success', value: 'true' },
    })
    expect(onFilterChange).toHaveBeenCalledWith({
      success: 'true',
      rocket: 'all',
      dateRange: { start: '', end: '' },
    })
  })

  it('calls onFilterChange when rocket filter changes', () => {
    render(
      <LaunchFilters
        onFilterChange={onFilterChange}
        onSortChange={onSortChange}
      />
    )
    fireEvent.change(screen.getByTestId('rocket-select'), {
      target: { name: 'rocket', value: '5e9d0d95eda69955f709d1eb' },
    })
    expect(onFilterChange).toHaveBeenCalledWith({
      success: 'all',
      rocket: '5e9d0d95eda69955f709d1eb',
      dateRange: { start: '', end: '' },
    })
  })

  it('calls onFilterChange when start date changes', () => {
    render(
      <LaunchFilters
        onFilterChange={onFilterChange}
        onSortChange={onSortChange}
      />
    )
    fireEvent.change(screen.getByTestId('start-date-input'), {
      target: { name: 'dateRange.start', value: '2021-01-01' },
    })
    expect(onFilterChange).toHaveBeenCalledWith({
      success: 'all',
      rocket: 'all',
      dateRange: { start: '2021-01-01', end: '' },
    })
  })

  it('calls onFilterChange when end date changes', () => {
    render(
      <LaunchFilters
        onFilterChange={onFilterChange}
        onSortChange={onSortChange}
      />
    )
    fireEvent.change(screen.getByTestId('end-date-input'), {
      target: { name: 'dateRange.end', value: '2021-12-31' },
    })
    expect(onFilterChange).toHaveBeenCalledWith({
      success: 'all',
      rocket: 'all',
      dateRange: { start: '', end: '2021-12-31' },
    })
  })

  it('calls onSortChange when sort option changes', () => {
    render(
      <LaunchFilters
        onFilterChange={onFilterChange}
        onSortChange={onSortChange}
      />
    )
    fireEvent.change(screen.getByTestId('sort-select'), {
      target: { value: 'name_asc' },
    })
    expect(onSortChange).toHaveBeenCalledWith('name_asc')
  })
})
