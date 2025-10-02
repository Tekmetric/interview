import { nanoid } from 'nanoid'
import { Filter, FilterType, FilterOperator } from '@/components/ui/filters'
import { RO_STATUS } from '@shared/constants'
import type { RepairOrderStatus } from '@shared/types'

/**
 * Parse a filter from URL search params
 * Format: ?filter=overdue | ?filter=status:waiting-parts
 */
export function parseFilterFromUrl(searchParams: URLSearchParams): Filter | null {
  const filterParam = searchParams.get('filter')
  if (!filterParam) return null

  // Handle special "overdue" filter
  if (filterParam === 'overdue') {
    return {
      id: nanoid(),
      type: FilterType.OVERDUE,
      operator: FilterOperator.IS,
      value: ['Overdue'],
    }
  }

  // Handle status filters: status:waiting-parts
  if (filterParam.startsWith('status:')) {
    const statusSlug = filterParam.replace('status:', '')
    const statusMapping: Record<string, { name: string; value: RepairOrderStatus }> = {
      'waiting-parts': { name: 'Waiting Parts', value: RO_STATUS.WAITING_PARTS },
      'awaiting-approval': {
        name: 'Awaiting Approval',
        value: RO_STATUS.AWAITING_APPROVAL,
      },
      new: { name: 'New', value: RO_STATUS.NEW },
      'in-progress': { name: 'In Progress', value: RO_STATUS.IN_PROGRESS },
      completed: { name: 'Completed', value: RO_STATUS.COMPLETED },
    }

    const status = statusMapping[statusSlug]
    if (status) {
      return {
        id: nanoid(),
        type: FilterType.STATUS,
        operator: FilterOperator.IS,
        value: [status.name],
      }
    }
  }

  return null
}

/**
 * Create a filter URL parameter
 */
export function createFilterUrl(
  filterType: 'overdue' | 'status',
  statusSlug?: string,
): string {
  if (filterType === 'overdue') {
    return '/kanban?filter=overdue'
  }

  if (filterType === 'status' && statusSlug) {
    return `/kanban?filter=status:${statusSlug}`
  }

  return '/kanban'
}
