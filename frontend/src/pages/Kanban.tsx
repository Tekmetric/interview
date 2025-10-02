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
import { REPAIR_ORDER_LABELS, API_ENDPOINTS, RO_STATUS, NAV_LABELS, COMMON_LABELS, KANBAN_LABELS } from '@shared/constants'
import { SelectionProvider } from '@/contexts/selection-context'
import { useMultiSelectKeyboard } from '@/hooks/use-multi-select'
import { BulkActionsBar } from '@/components/kanban/bulk-actions-bar'
import { Filter, FilterType } from '@/components/ui/filters'
import { SettingsDialog } from '@/components/settings/settings-dialog'
import { Button } from '@/components/ui/button'
import { Plus, Settings } from 'lucide-react'
import { useSearch } from 'wouter'
import { parseFilterFromUrl } from '@/lib/filter-utils'

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

  // Parse URL filter on mount
  useEffect(() => {
    const filterFromUrl = parseFilterFromUrl(searchParams)
    if (filterFromUrl) {
      setFilters([filterFromUrl])
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

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

  // Enable keyboard shortcuts for multi-select
  const filteredOrderIds = useMemo(
    () => filteredOrders.map((o) => o.id),
    [filteredOrders],
  )
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
      <div className='flex flex-col gap-4 p-6'>
        <header className='flex items-center justify-between gap-2'>
          <div className='flex items-center gap-2'>
            <h1 className='text-2xl font-bold text-gray-900'>{KANBAN_LABELS.TITLE}</h1>
            <Button
              variant='ghost'
              size='icon'
              onClick={() => setSettingsOpen(true)}
              className='h-8 w-8'
            >
              <Settings className='h-4 w-4' />
              <span className='sr-only'>{NAV_LABELS.SETTINGS}</span>
            </Button>
          </div>
          <Button onClick={() => setLocation('/kanban?createRO=true')}>
            <Plus className='h-4 w-4' />
            {REPAIR_ORDER_LABELS.NEW_ORDER}
          </Button>
        </header>

        <KanbanFilters
          filters={filters}
          onFiltersChange={setFilters}
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
          technicians={technicians}
        />

        <KanbanBoard orders={filteredOrders} onStatusChange={handleStatusChange} />
        <BulkActionsBar orders={filteredOrders} />
      </div>
      <SettingsDialog open={settingsOpen} onOpenChange={setSettingsOpen} />
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
