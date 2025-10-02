import { Suspense, useMemo, useState, useEffect, useRef } from 'react'
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
import type { RepairOrderStatus } from '@shared/types'
import { KANBAN_LABELS, REPAIR_ORDER_LABELS, API_ENDPOINTS } from '@shared/constants'
import { usePreferences } from '@/hooks/use-preferences'
import { SelectionProvider } from '@/contexts/selection-context'
import { useMultiSelectKeyboard } from '@/hooks/use-multi-select'
import { BulkActionsBar } from '@/components/kanban/bulk-actions-bar'

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
  const { preferences, getPresetById } = usePreferences()

  const [searchQuery, setSearchQuery] = useState('')
  const [priorityFilter, setPriorityFilter] = useState<'ALL' | 'HIGH' | 'NORMAL'>('ALL')
  const [techFilter, setTechFilter] = useState('ALL')

  const filteredOrders = useMemo(() => {
    return orders.filter((order) => {
      // Search filter
      const searchLower = searchQuery.toLowerCase()
      const matchesSearch =
        !searchQuery ||
        order.id.toLowerCase().includes(searchLower) ||
        order.customer.name.toLowerCase().includes(searchLower) ||
        `${order.vehicle.year} ${order.vehicle.make} ${order.vehicle.model}`
          .toLowerCase()
          .includes(searchLower)

      // Priority filter
      const matchesPriority =
        priorityFilter === 'ALL' || order.priority === priorityFilter

      // Tech filter
      const matchesTech =
        techFilter === 'ALL' ||
        (techFilter === 'UNASSIGNED' && !order.assignedTech) ||
        order.assignedTech?.id === techFilter

      return matchesSearch && matchesPriority && matchesTech
    })
  }, [orders, searchQuery, priorityFilter, techFilter])

  // Enable keyboard shortcuts for multi-select
  const filteredOrderIds = useMemo(
    () => filteredOrders.map((o) => o.id),
    [filteredOrders],
  )
  useMultiSelectKeyboard(filteredOrderIds)

  // Apply default preset on mount
  const hasLoadedRef = useRef(false)
  useEffect(() => {
    if (hasLoadedRef.current) return
    hasLoadedRef.current = true
    if (preferences.defaultFilterPreset) {
      const defaultPreset = getPresetById(preferences.defaultFilterPreset)
      if (defaultPreset) {
        setSearchQuery(defaultPreset.searchQuery)
        setPriorityFilter(defaultPreset.priorityFilter)
        setTechFilter(defaultPreset.techFilter)
      }
    }
  }, [preferences.defaultFilterPreset, getPresetById])

  const mutation = useMutation({
    mutationFn: ({ orderId, status }: { orderId: string; status: RepairOrderStatus }) =>
      updateOrderStatus(orderId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success(REPAIR_ORDER_LABELS.STATUS_UPDATED)
    },
    onError: (err: Error) => {
      toast.error(err.message || REPAIR_ORDER_LABELS.FAILED_TO_UPDATE_STATUS)
    },
  })

  const handleStatusChange = (orderId: string, newStatus: RepairOrderStatus) => {
    mutation.mutate({ orderId, status: newStatus })
  }

  const handleApplyPreset = (filters: {
    searchQuery: string
    priorityFilter: 'ALL' | 'HIGH' | 'NORMAL'
    techFilter: string
  }) => {
    setSearchQuery(filters.searchQuery)
    setPriorityFilter(filters.priorityFilter)
    setTechFilter(filters.techFilter)
  }

  return (
    <AppLayout>
      <div className='flex flex-col gap-4 p-6'>
        <header className='flex flex-col gap-2'>
          <h1 className='text-2xl font-bold text-gray-900'>{KANBAN_LABELS.TITLE}</h1>
        </header>

        <KanbanFilters
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
          priorityFilter={priorityFilter}
          onPriorityChange={setPriorityFilter}
          techFilter={techFilter}
          onTechChange={setTechFilter}
          technicians={technicians}
          onApplyPreset={handleApplyPreset}
        />

        <KanbanBoard orders={filteredOrders} onStatusChange={handleStatusChange} />
        <BulkActionsBar orders={filteredOrders} />
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
    </ErrorBoundary>
  )
}
