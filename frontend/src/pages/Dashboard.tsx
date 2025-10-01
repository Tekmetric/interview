import { KPICard } from '@/components/dashboard/kpi-card'
import { Button } from '@/components/ui/button'
import { Skeleton } from '@/components/ui/skeleton'
import { useRepairOrders } from '@/hooks/useRepairOrders'
import { calculateKPIs } from '@/lib/kpi-utils'

export function Dashboard() {
  const { data: orders, isLoading, error } = useRepairOrders()

  if (isLoading) {
    return (
      <div className='min-h-screen bg-gray-50 p-8'>
        <div className='mx-auto max-w-6xl space-y-6'>
          <header className='space-y-2'>
            <h1 className='text-3xl font-bold text-gray-900'>Dashboard</h1>
            <p className='text-gray-600'>Quick overview of repair orders</p>
          </header>

          <div className='grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4'>
            {[1, 2, 3, 4].map((i) => (
              <Skeleton key={i} className='h-32' />
            ))}
          </div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className='min-h-screen bg-gray-50 p-8'>
        <div className='mx-auto max-w-6xl'>
          <div className='rounded-lg bg-red-50 p-4 text-red-800'>
            Error loading repair orders. Please try again.
          </div>
        </div>
      </div>
    )
  }

  const kpis = calculateKPIs(orders || [])

  return (
    <div className='min-h-screen bg-gray-50 p-8'>
      <div className='mx-auto max-w-6xl space-y-6'>
        <header className='space-y-2'>
          <h1 className='text-3xl font-bold text-gray-900'>Dashboard</h1>
          <p className='text-gray-600'>Quick overview of repair orders</p>
        </header>

        <div className='grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4'>
          <KPICard
            title='Total WIP'
            value={kpis.totalWIP}
            icon={
              <svg
                xmlns='http://www.w3.org/2000/svg'
                viewBox='0 0 24 24'
                fill='none'
                stroke='currentColor'
                strokeLinecap='round'
                strokeLinejoin='round'
                strokeWidth='2'
                className='h-4 w-4'
              >
                <path d='M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' />
                <circle cx='9' cy='7' r='4' />
                <path d='M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75' />
              </svg>
            }
          />

          <KPICard
            title='Overdue'
            value={kpis.overdueCount}
            icon={
              <svg
                xmlns='http://www.w3.org/2000/svg'
                viewBox='0 0 24 24'
                fill='none'
                stroke='currentColor'
                strokeLinecap='round'
                strokeLinejoin='round'
                strokeWidth='2'
                className='h-4 w-4'
              >
                <circle cx='12' cy='12' r='10' />
                <polyline points='12 6 12 12 16 14' />
              </svg>
            }
          />

          <KPICard
            title='Waiting Parts'
            value={kpis.waitingPartsCount}
            icon={
              <svg
                xmlns='http://www.w3.org/2000/svg'
                viewBox='0 0 24 24'
                fill='none'
                stroke='currentColor'
                strokeLinecap='round'
                strokeLinejoin='round'
                strokeWidth='2'
                className='h-4 w-4'
              >
                <path d='M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z' />
                <polyline points='3.27 6.96 12 12.01 20.73 6.96' />
                <line x1='12' y1='22.08' x2='12' y2='12' />
              </svg>
            }
          />

          <KPICard
            title='Awaiting Approval'
            value={kpis.awaitingApprovalCount}
            icon={
              <svg
                xmlns='http://www.w3.org/2000/svg'
                viewBox='0 0 24 24'
                fill='none'
                stroke='currentColor'
                strokeLinecap='round'
                strokeLinejoin='round'
                strokeWidth='2'
                className='h-4 w-4'
              >
                <path d='M9 11l3 3L22 4' />
                <path d='M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11' />
              </svg>
            }
          />
        </div>

        <div className='flex gap-4'>
          <Button size='lg'>Open Kanban Board</Button>
          <Button variant='outline' size='lg'>
            Create RO
          </Button>
        </div>
      </div>
    </div>
  )
}
