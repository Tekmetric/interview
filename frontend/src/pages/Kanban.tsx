import { Suspense, useMemo, useState, useEffect } from 'react'
import { useLocation } from 'wouter'
import { AppLayout } from '@/components/layout/app-layout'
import { KanbanBoard } from '@/components/kanban/kanban-board'
import { KanbanFilters } from '@/components/kanban/kanban-filters'
import { Skeleton } from '@/components/ui/skeleton'
import { ErrorBoundary } from '@/components/ErrorBoundary'
import { useRepairOrders } from '@/components/repair-order/hooks/useRepairOrders'
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'
import { RODetailsDrawer } from '@/components/repair-order/ro-details-drawer'
import { ROCreateDrawer } from '@/components/repair-order/ro-create-drawer'
import { REPAIR_ORDER_LABELS, NAV_LABELS, KANBAN_LABELS } from '@shared/constants'
import { SelectionProvider } from '@/contexts/selection-context'
import { useMultiSelectKeyboard } from '@/hooks/use-multi-select'
import { BulkActionsBar } from '@/components/kanban/bulk-actions-bar'
import { SettingsPopover } from '@/components/settings/settings-popover'
import { Button } from '@/components/ui/button'
import { Plus, Settings } from 'lucide-react'
import { useKanbanFilters } from '@/hooks/use-kanban-filters'
import { useKanbanUrlState } from '@/hooks/use-kanban-url-state'
import { useOrderMutation } from '@/hooks/use-order-mutation'
import { useSyncSearchToUrl } from '@/hooks/use-kanban-url-state'

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
  const [settingsOpen, setSettingsOpen] = useState(false)

  const {
    filters,
    setFilters,
    searchQuery,
    setSearchQuery,
    sortBy,
    setSortBy,
    sortedOrders,
  } = useKanbanFilters(orders)

  const { initializeFromUrl } = useKanbanUrlState()
  const { updateOrderStatus } = useOrderMutation()

  useEffect(() => {
    initializeFromUrl(setFilters, setSearchQuery)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useSyncSearchToUrl(searchQuery)

  // Enable keyboard shortcuts for multi-select
  const filteredOrderIds = useMemo(() => sortedOrders.map((o) => o.id), [sortedOrders])
  useMultiSelectKeyboard(filteredOrderIds)

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

        <KanbanBoard orders={sortedOrders} onStatusChange={updateOrderStatus} />
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
