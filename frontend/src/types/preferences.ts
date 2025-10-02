export interface ColumnVisibility {
  customerPhone: boolean
  vehicleDetails: boolean
  assignedTech: boolean
  dueTime: boolean
  services: boolean
}

export interface SavedFilter {
  id: string
  name: string
  searchQuery: string
  priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
  techFilter: string
  createdAt: string
}

export interface UserPreferences {
  columnVisibility: ColumnVisibility
  savedFilters: SavedFilter[]
  defaultFilterPreset?: string
}

export const DEFAULT_PREFERENCES: UserPreferences = {
  columnVisibility: {
    customerPhone: true,
    vehicleDetails: true,
    assignedTech: true,
    dueTime: true,
    services: true,
  },
  savedFilters: [],
  defaultFilterPreset: undefined,
}
