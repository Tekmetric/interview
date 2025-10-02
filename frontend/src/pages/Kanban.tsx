import { Suspense, useMemo, useState } from 'react'
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
import type { RepairOrder, RepairOrderStatus } from '@shared/types'
import { KANBAN_LABELS, REPAIR_ORDER_LABELS, API_ENDPOINTS, RO_STATUS, NAV_LABELS } from '@shared/constants'
import { SelectionProvider } from '@/contexts/selection-context'
import { useMultiSelectKeyboard } from '@/hooks/use-multi-select'
import { BulkActionsBar } from '@/components/kanban/bulk-actions-bar'
import { Filter, FilterType } from '@/components/ui/filters'
import { SettingsDialog } from '@/components/settings/settings-dialog'
import { Button } from '@/components/ui/button'

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
          <p className='text-gray-600'>{KANBAN_LABELS.SUBTITLE}</p>
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
  const { data: orders } = useRepairOrders()
  const { data: technicians } = useTechnicians()
  const queryClient = useQueryClient()
  const [settingsOpen, setSettingsOpen] = useState(false)

  const [filters, setFilters] = useState<Filter[]>([])

  const filteredOrders = useMemo(() => {
    // Map filter display names to backend values
    const localStatusMapping: Record<string, RepairOrderStatus> = {
      New: RO_STATUS.NEW,
      'Awaiting Approval': RO_STATUS.AWAITING_APPROVAL,
      'In Progress': RO_STATUS.IN_PROGRESS,
      'Waiting Parts': RO_STATUS.WAITING_PARTS,
      Completed: RO_STATUS.COMPLETED,
    }

    return orders.filter((order) => {
      // Apply each filter
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
              if (name === 'Unassigned') {
                return !order.assignedTech
              }
              return order.assignedTech?.name === name
            })
          }
          default:
            return true
        }
      })
    })
  }, [orders, filters])

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
          <h1 className='text-2xl font-bold text-gray-900'>{KANBAN_LABELS.TITLE}</h1>
          <Button variant='outline' onClick={() => setSettingsOpen(true)}>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='mr-2 h-5 w-5'
            >
              <circle cx='12' cy='12' r='3' />
              <path d='M12 1v6m0 6v6M4.22 4.22l4.24 4.24m5.08 5.08l4.24 4.24M1 12h6m6 0h6M4.22 19.78l4.24-4.24m5.08-5.08l4.24-4.24' />
            </svg>
            {NAV_LABELS.SETTINGS}
          </Button>
        </header>

        <KanbanFilters filters={filters} onFiltersChange={setFilters} technicians={technicians} />

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
    </ErrorBoundary>
  )
}
