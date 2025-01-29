import { useState } from 'react'

interface LaunchFiltersProps {
  onFilterChange: (filters: {
    success: string
    rocket: string
    dateRange: { start: string; end: string }
  }) => void
  onSortChange: (sort: string) => void
}

function LaunchFilters({
  onFilterChange,
  onSortChange,
}: LaunchFiltersProps): React.ReactElement {
  const [filters, setFilters] = useState({
    success: 'all',
    rocket: 'all',
    dateRange: { start: '', end: '' },
  })
  const [sort, setSort] = useState('date_desc')

  const handleFilterChange = (
    e: React.ChangeEvent<HTMLSelectElement | HTMLInputElement>
  ): void => {
    const { name, value } = e.target
    let newFilters = { ...filters }

    if (name.startsWith('dateRange.')) {
      const field = name.split('.')[1]
      newFilters = {
        ...filters,
        dateRange: {
          ...filters.dateRange,
          [field]: value,
        },
      }
    } else {
      newFilters = {
        ...filters,
        [name]: value,
      }
    }

    setFilters(newFilters)
    onFilterChange(newFilters)
  }

  const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>): void => {
    const newSort = e.target.value
    setSort(newSort)
    onSortChange(newSort)
  }

  return (
    <div
      className="mb-6 p-4 bg-gray-800/80 rounded-lg shadow-lg border border-gray-700"
      data-testid="launch-filters"
    >
      <h3
        className="text-lg font-semibold mb-4 text-gray-200"
        data-testid="filters-title"
      >
        Filters
      </h3>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <label
            htmlFor="success"
            className="block text-sm text-gray-400 mb-1"
            data-testid="success-label"
          >
            Launch Outcome
          </label>
          <div className="relative">
            <select
              id="success"
              name="success"
              value={filters.success}
              onChange={handleFilterChange}
              className="w-full bg-gray-900 border border-gray-700 rounded-md py-2 pl-3 pr-8 text-gray-200 focus:border-primary focus:ring-1 focus:ring-primary appearance-none"
              data-testid="success-select"
            >
              <option value="all">All</option>
              <option value="true">Success</option>
              <option value="false">Failure</option>
            </select>
            <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
              <svg
                className="h-4 w-4 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </div>
          </div>
        </div>

        <div>
          <label
            htmlFor="rocket"
            className="block text-sm text-gray-400 mb-1"
            data-testid="rocket-label"
          >
            Rocket Type
          </label>
          <div className="relative">
            <select
              id="rocket"
              name="rocket"
              value={filters.rocket}
              onChange={handleFilterChange}
              className="w-full bg-gray-900 border border-gray-700 rounded-md py-2 pl-3 pr-8 text-gray-200 focus:border-primary focus:ring-1 focus:ring-primary appearance-none"
              data-testid="rocket-select"
            >
              <option value="all">All</option>
              <option value="5e9d0d95eda69955f709d1eb">Falcon 9</option>
              <option value="5e9d0d95eda69974db09d1ed">Falcon Heavy</option>
              <option value="5e9d0d96eda699382d09d1ee">Starship</option>
            </select>
            <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
              <svg
                className="h-4 w-4 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M19 9l-7 7-7-7"
                />
              </svg>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <label
            htmlFor="dateRange.start"
            className="block text-sm text-gray-400 mb-1"
            data-testid="start-date-label"
          >
            Start Date
          </label>
          <div className="relative">
            <input
              type="date"
              id="dateRange.start"
              name="dateRange.start"
              value={filters.dateRange.start}
              onChange={handleFilterChange}
              className="w-full bg-gray-900 border border-gray-700 rounded-md py-2 px-3 text-gray-200 focus:border-primary focus:ring-1 focus:ring-primary"
              data-testid="start-date-input"
            />
          </div>
        </div>
        <div>
          <label
            htmlFor="dateRange.end"
            className="block text-sm text-gray-400 mb-1"
            data-testid="end-date-label"
          >
            End Date
          </label>
          <div className="relative">
            <input
              type="date"
              id="dateRange.end"
              name="dateRange.end"
              value={filters.dateRange.end}
              onChange={handleFilterChange}
              className="w-full bg-gray-900 border border-gray-700 rounded-md py-2 px-3 text-gray-200 focus:border-primary focus:ring-1 focus:ring-primary"
              data-testid="end-date-input"
            />
          </div>
        </div>
      </div>

      <div>
        <label
          htmlFor="sort"
          className="block text-sm text-gray-400 mb-1"
          data-testid="sort-label"
        >
          Sort By
        </label>
        <div className="relative">
          <select
            id="sort"
            name="sort"
            value={sort}
            onChange={handleSortChange}
            className="w-full bg-gray-900 border border-gray-700 rounded-md py-2 pl-3 pr-8 text-gray-200 focus:border-primary focus:ring-1 focus:ring-primary appearance-none"
            data-testid="sort-select"
          >
            <option value="date_desc">Date (Newest First)</option>
            <option value="date_asc">Date (Oldest First)</option>
            <option value="name_asc">Name (A-Z)</option>
            <option value="name_desc">Name (Z-A)</option>
          </select>
          <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
            <svg
              className="h-4 w-4 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M19 9l-7 7-7-7"
              />
            </svg>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LaunchFilters
