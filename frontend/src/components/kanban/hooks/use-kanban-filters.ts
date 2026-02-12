import { useMemo, useState } from 'react'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import { Filter, FilterType } from '@/components/ui/filters'
import { RO_STATUS, KANBAN_LABELS, COMMON_LABELS, FILTER_LABELS } from '@shared/constants'

export type SortOption =
  | 'default'
  | 'dueTime'
  | 'dueTimeDesc'
  | 'customer'
  | 'customerDesc'
  | 'vehicle'
  | 'vehicleDesc'

type UseKanbanFiltersReturn = {
  filters: Filter[]
  setFilters: (filters: Filter[]) => void
  searchQuery: string
  setSearchQuery: (query: string) => void
  sortBy: SortOption
  setSortBy: (sortBy: SortOption) => void
  filteredOrders: RepairOrder[]
  sortedOrders: RepairOrder[]
}

/**
 * Business logic for filtering and sorting repair orders in Kanban view.
 * Handles status mapping, search matching, filter evaluation, and multi-key sorting.
 */
export function useKanbanFilters(orders: RepairOrder[]): UseKanbanFiltersReturn {
  const [filters, setFilters] = useState<Filter[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [sortBy, setSortBy] = useState<SortOption>('default')

  // Filter orders by search query and active filters
  const filteredOrders = useMemo(() => {
    // Map filter display names to backend values
    const localStatusMapping: Record<string, RepairOrderStatus> = {
      [KANBAN_LABELS.STATUS.NEW]: RO_STATUS.NEW,
      [KANBAN_LABELS.STATUS.AWAITING_APPROVAL]: RO_STATUS.AWAITING_APPROVAL,
      [KANBAN_LABELS.STATUS.IN_PROGRESS]: RO_STATUS.IN_PROGRESS,
      [KANBAN_LABELS.STATUS.WAITING_PARTS]: RO_STATUS.WAITING_PARTS,
      [KANBAN_LABELS.STATUS.COMPLETED]: RO_STATUS.COMPLETED,
    }

    const localPriorityMapping: Record<string, string> = {
      [FILTER_LABELS.HIGH_PRIORITY]: 'HIGH',
      [FILTER_LABELS.NORMAL_PRIORITY]: 'NORMAL',
    }

    return orders.filter((order) => {
      // Search matching: order ID, customer name, vehicle details
      const searchLower = searchQuery.toLowerCase()
      const matchesSearch =
        !searchQuery ||
        order.id.toLowerCase().includes(searchLower) ||
        order.customer.name.toLowerCase().includes(searchLower) ||
        `${order.vehicle.year} ${order.vehicle.make} ${order.vehicle.model}`
          .toLowerCase()
          .includes(searchLower)

      if (!matchesSearch) return false

      // Filter evaluation: all filters must pass
      return filters.every((filter) => {
        switch (filter.type) {
          case FilterType.STATUS: {
            const statusValues = filter.value.map((v) => localStatusMapping[v])
            return statusValues.includes(order.status)
          }
          case FilterType.PRIORITY: {
            const priorityValues = filter.value.map((v) => localPriorityMapping[v])
            return priorityValues.includes(order.priority)
          }
          case FilterType.TECHNICIAN: {
            return filter.value.some((name) => {
              if (name === COMMON_LABELS.UNASSIGNED) {
                return !order.assignedTech
              }
              return order.assignedTech?.name === name
            })
          }
          case FilterType.OVERDUE: {
            const now = new Date()
            return (
              order.dueTime &&
              new Date(order.dueTime) < now &&
              order.status !== RO_STATUS.COMPLETED
            )
          }
          default:
            return true
        }
      })
    })
  }, [orders, filters, searchQuery])

  // Sort filtered orders by selected sort option
  const sortedOrders = useMemo(() => {
    if (sortBy === 'default') {
      return filteredOrders
    }

    return [...filteredOrders].sort((a, b) => {
      switch (sortBy) {
        case 'dueTime':
          // Earliest first, nulls last
          if (!a.dueTime) return 1
          if (!b.dueTime) return -1
          return new Date(a.dueTime).getTime() - new Date(b.dueTime).getTime()

        case 'dueTimeDesc':
          // Latest first, nulls last
          if (!a.dueTime) return 1
          if (!b.dueTime) return -1
          return new Date(b.dueTime).getTime() - new Date(a.dueTime).getTime()

        case 'customer':
          // A-Z by customer name
          return a.customer.name.localeCompare(b.customer.name)

        case 'customerDesc':
          // Z-A by customer name
          return b.customer.name.localeCompare(a.customer.name)

        case 'vehicle':
          // A-Z by make then model
          return `${a.vehicle.make} ${a.vehicle.model}`.localeCompare(
            `${b.vehicle.make} ${b.vehicle.model}`,
          )

        case 'vehicleDesc':
          // Z-A by make then model
          return `${b.vehicle.make} ${b.vehicle.model}`.localeCompare(
            `${a.vehicle.make} ${a.vehicle.model}`,
          )

        default:
          return 0
      }
    })
  }, [filteredOrders, sortBy])

  return {
    filters,
    setFilters,
    searchQuery,
    setSearchQuery,
    sortBy,
    setSortBy,
    filteredOrders,
    sortedOrders,
  }
}
