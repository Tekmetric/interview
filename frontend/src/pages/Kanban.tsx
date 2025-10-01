import { Suspense, useMemo, useState } from 'react'
import { AppLayout } from '@/components/layout/app-layout'
import { KanbanBoard } from '@/components/kanban/kanban-board'
import { KanbanFilters } from '@/components/kanban/kanban-filters'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorBoundary } from '@/components/ErrorBoundary'
import { useRepairOrders } from '@/hooks/useRepairOrders'
import { useTechnicians } from '@/hooks/useTechnicians'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import { RODetailsDrawer } from '@/components/repair-order/ro-details-drawer'
import type { RepairOrderStatus } from '@shared/types'

async function updateOrderStatus(orderId: string, status: RepairOrderStatus) {
  const res = await fetch(`/api/repairOrders/${orderId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status }),
  })

  if (!res.ok) {
    const error = await res.json()
    throw new Error(error.message || 'Failed to update order status')
  }

  return res.json()
}

function KanbanLoading() {
  return (
    <AppLayout>
      <div className='flex flex-col gap-6 p-8'>
        <header className='flex flex-col gap-2'>
          <h1 className='text-3xl font-bold text-gray-900'>Kanban Board</h1>
          <p className='text-gray-600'>Drag and drop to update repair order status</p>
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
          Error loading repair orders. Please try again.
        </div>
      </div>
    </AppLayout>
  )
}

function KanbanContent() {
  const { data: orders } = useRepairOrders()
  const { data: technicians } = useTechnicians()
  const queryClient = useQueryClient()

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
      const matchesPriority = priorityFilter === 'ALL' || order.priority === priorityFilter

      // Tech filter
      const matchesTech =
        techFilter === 'ALL' ||
        (techFilter === 'UNASSIGNED' && !order.assignedTech) ||
        order.assignedTech?.id === techFilter

      return matchesSearch && matchesPriority && matchesTech
    })
  }, [orders, searchQuery, priorityFilter, techFilter])

  const mutation = useMutation({
    mutationFn: ({ orderId, status }: { orderId: string; status: RepairOrderStatus }) =>
      updateOrderStatus(orderId, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['repairOrders'] })
      toast.success('Order status updated')
    },
    onError: (err: Error) => {
      toast.error(err.message || 'Failed to update status')
    },
  })

  const handleStatusChange = (orderId: string, newStatus: RepairOrderStatus) => {
    mutation.mutate({ orderId, status: newStatus })
  }

  return (
    <AppLayout>
      <div className='flex flex-col gap-4 p-6'>
        <header className='flex flex-col gap-2'>
          <h1 className='text-2xl font-bold text-gray-900'>Kanban Board</h1>
          <p className='text-sm text-gray-600'>Drag and drop to update repair order status</p>
        </header>

        <KanbanFilters
          searchQuery={searchQuery}
          onSearchChange={setSearchQuery}
          priorityFilter={priorityFilter}
          onPriorityChange={setPriorityFilter}
          techFilter={techFilter}
          onTechChange={setTechFilter}
          technicians={technicians}
        />

        <KanbanBoard orders={filteredOrders} onStatusChange={handleStatusChange} />
      </div>
    </AppLayout>
  )
}

export function Kanban() {
  return (
    <ErrorBoundary fallback={() => <KanbanError />}>
      <Suspense fallback={<KanbanLoading />}>
        <KanbanContent />
      </Suspense>
      <RODetailsDrawer />
    </ErrorBoundary>
  )
}
