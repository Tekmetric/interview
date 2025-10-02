import { Suspense, useMemo, useState, useEffect } from 'react'
import { useLocation } from 'wouter'
import { AppLayout } from '@/components/layout/app-layout'
import { KanbanBoard } from '@/components/kanban/kanban-board'
import { KanbanFilters } from '@/components/kanban/kanban-filters'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorBoundary } from '@/components/ErrorBoundary'
import { useRepairOrders } from '@/components/repair-order/hooks/useRepairOrders'
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { RODetailsDrawer } from '@/components/repair-order/ro-details-drawer'
import { ROCreateDrawer } from '@/components/repair-order/ro-create-drawer'
import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import {
  REPAIR_ORDER_LABELS,
  API_ENDPOINTS,
  RO_STATUS,
  NAV_LABELS,
  COMMON_LABELS,
  KANBAN_LABELS,
} from '@shared/constants'
import { SelectionProvider } from '@/contexts/selection-context'
import { useMultiSelectKeyboard } from '@/hooks/use-multi-select'
import { BulkActionsBar } from '@/components/kanban/bulk-actions-bar'
import { Filter, FilterType } from '@/components/ui/filters'
import { SettingsPopover } from '@/components/settings/settings-popover'
import { Button } from '@/components/ui/button'
import { Plus, Settings } from 'lucide-react'
import { useSearch } from 'wouter'
import {
  parseFilterFromUrl,
  parseSearchFromUrl,
  buildUrlWithSearch,
} from '@/lib/filter-utils'

async function updateOrderStatus(orderId: string, status: RepairOrderStatus) {
  const res = await fetch(API_ENDPOINTS.REPAIR_ORDERS.BY_ID(orderId), {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status }),
  })

  if (!res.ok) {
    const error = await res.json()
    throw new Error(error.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE_STATUS)
  }

  return res.json()
}

function KanbanLoading() {
  return (
    <AppLayout>
      <div className='flex flex-col gap-6 p-8'>
        <header className='flex flex-col gap-2'>
          <h1 className='text-3xl font-bold text-gray-900'>{KANBAN_LABELS.TITLE}</h1>
        </header>
        <div className='flex gap-4'>
          {[1, 2, 3, 4, 5].map((i) => (
            <Skeleton key={i} className='h-96 min-w-[300px]' />
          ))}
        </div>
      </div>
    </AppLayout>
  )
}

function KanbanError() {
  return (
    <AppLayout>
      <div className='p-8'>
        <div className='rounded-lg bg-red-50 p-4 text-red-800'>
          {REPAIR_ORDER_LABELS.ERROR_LOADING_LIST}
        </div>
      </div>
    </AppLayout>
  )
}

function KanbanContent() {
  const [, setLocation] = useLocation()
  const { data: orders } = useRepairOrders()
  const { data: technicians } = useTechnicians()
  const queryClient = useQueryClient()
  const [settingsOpen, setSettingsOpen] = useState(false)
  const searchParams = new URLSearchParams(useSearch())

  const [filters, setFilters] = useState<Filter[]>([])
  const [searchQuery, setSearchQuery] = useState('')
  const [sortBy, setSortBy] = useState<
    | 'default'
    | 'dueTime'
    | 'dueTimeDesc'
    | 'customer'
    | 'customerDesc'
    | 'vehicle'
    | 'vehicleDesc'
  >('default')

  // Parse URL filter and search on mount
  useEffect(() => {
    const filterFromUrl = parseFilterFromUrl(searchParams)
    if (filterFromUrl) {
      setFilters([filterFromUrl])
    }

    const searchFromUrl = parseSearchFromUrl(searchParams)
    if (searchFromUrl) {
      setSearchQuery(searchFromUrl)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // Update URL when search changes (debounced)
  useEffect(() => {
    const timeoutId = setTimeout(() => {
      const urlParams = buildUrlWithSearch(searchParams, searchQuery)
      setLocation(`?${urlParams}`, { replace: true })
    }, 500) // Debounce to avoid too many URL updates

    return () => clearTimeout(timeoutId)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchQuery])

  const filteredOrders = useMemo(() => {
    // Map filter display names to backend values
    const localStatusMapping: Record<string, RepairOrderStatus> = {
      [KANBAN_LABELS.STATUS.NEW]: RO_STATUS.NEW,
      [KANBAN_LABELS.STATUS.AWAITING_APPROVAL]: RO_STATUS.AWAITING_APPROVAL,
      [KANBAN_LABELS.STATUS.IN_PROGRESS]: RO_STATUS.IN_PROGRESS,
      [KANBAN_LABELS.STATUS.WAITING_PARTS]: RO_STATUS.WAITING_PARTS,
      [KANBAN_LABELS.STATUS.COMPLETED]: RO_STATUS.COMPLETED,
    }

    return orders.filter((order) => {
      const searchLower = searchQuery.toLowerCase()
      const matchesSearch =
        !searchQuery ||
        order.id.toLowerCase().includes(searchLower) ||
        order.customer.name.toLowerCase().includes(searchLower) ||
        `${order.vehicle.year} ${order.vehicle.make} ${order.vehicle.model}`
          .toLowerCase()
          .includes(searchLower)

      if (!matchesSearch) return false

      return filters.every((filter) => {
        switch (filter.type) {
          case FilterType.STATUS: {
            const statusValues = filter.value.map((v) => localStatusMapping[v])
            return statusValues.includes(order.status)
          }
          case FilterType.PRIORITY: {
            const priorityValues = filter.value.map((v) => v.toUpperCase())
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

  // Sort orders by due time if requested
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
          // A-Z
          return a.customer.name.localeCompare(b.customer.name)

        case 'customerDesc':
          // Z-A
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

  // Enable keyboard shortcuts for multi-select
  const filteredOrderIds = useMemo(() => sortedOrders.map((o) => o.id), [sortedOrders])
  useMultiSelectKeyboard(filteredOrderIds)

  const mutation = useMutation({
    mutationFn: ({ orderId, status }: { orderId: string; status: RepairOrderStatus }) =>
      updateOrderStatus(orderId, status),
    onMutate: async ({ orderId, status }) => {
      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['repairOrders'] })

      // Snapshot previous value
      const previousOrders = queryClient.getQueryData<RepairOrder[]>(['repairOrders'])

      // Optimistically update cache
      if (previousOrders) {
        queryClient.setQueryData<RepairOrder[]>(
          ['repairOrders'],
          previousOrders.map((order) =>
            order.id === orderId ? { ...order, status } : order,
          ),
        )
      }

      return { previousOrders }
    },
    onError: (err: Error, _variables, context) => {
      // Rollback on error
      if (context?.previousOrders) {
        queryClient.setQueryData(['repairOrders'], context.previousOrders)
      }
      toast.error(err.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE_STATUS)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(REPAIR_ORDER_LABELS.STATUS_UPDATED)
    },
  })

  const handleStatusChange = (orderId: string, newStatus: RepairOrderStatus) => {
    mutation.mutate({ orderId, status: newStatus })
  }

  return (
    <AppLayout>
      <div className='flex flex-col gap-3 p-3 sm:gap-4 sm:p-4 lg:p-6'>
        <header className='flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between'>
          <div className='flex items-center gap-2'>
            <h1 className='text-xl font-bold text-gray-900 sm:text-2xl'>
              {KANBAN_LABELS.TITLE}
            </h1>
            <SettingsPopover open={settingsOpen} onOpenChange={setSettingsOpen}>
              <Button
                variant='ghost'
                size='icon'
                className='h-8 min-h-[44px] w-8 min-w-[44px] sm:h-8 sm:min-h-0 sm:w-8 sm:min-w-0'
                aria-label={NAV_LABELS.SETTINGS}
              >
                <Settings className='h-4 w-4' />
                <span className='sr-only'>{NAV_LABELS.SETTINGS}</span>
              </Button>
            </SettingsPopover>
          </div>
          <Button
            onClick={() => setLocation('/kanban?createRO=true')}
            className='min-h-[44px] sm:min-h-0'
          >
            <Plus className='h-4 w-4' />
            <span className='sm:inline'>{REPAIR_ORDER_LABELS.CREATE_NEW}</span>
          </Button>
        </header>

        <KanbanFilters
          filters={filters}
          onFiltersChange={setFilters}
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
          technicians={technicians}
          sortBy={sortBy}
          onSortChange={setSortBy}
        />

        <KanbanBoard orders={sortedOrders} onStatusChange={handleStatusChange} />
        <BulkActionsBar orders={sortedOrders} />
      </div>
    </AppLayout>
  )
}

export function Kanban() {
  return (
    <ErrorBoundary fallback={() => <KanbanError />}>
      <Suspense fallback={<KanbanLoading />}>
        <SelectionProvider>
          <KanbanContent />
        </SelectionProvider>
      </Suspense>
      <RODetailsDrawer />
      <ROCreateDrawer />
    </ErrorBoundary>
  )
}
