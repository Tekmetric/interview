import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useOverdueOrders, useRecentOrders } from '@/hooks/useQuickLists'
import { RepairOrderItem } from './repair-order-item'

export function QuickLists() {
  const { data: overdueOrders, isLoading: overdueLoading } = useOverdueOrders(5)
  const { data: recentOrders, isLoading: recentLoading } = useRecentOrders(5)

  return (
    <div className='grid grid-cols-1 gap-4 lg:grid-cols-2'>
      <Card>
        <CardHeader>
          <CardTitle className='flex items-center gap-2 text-lg'>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5 text-red-500'
            >
              <circle cx='12' cy='12' r='10' />
              <polyline points='12 6 12 12 16 14' />
            </svg>
            Top 5 Overdue
          </CardTitle>
        </CardHeader>
        <CardContent>
          {overdueLoading ? (
            <div className='flex flex-col gap-3'>
              {[1, 2, 3].map((i) => (
                <Skeleton key={i} className='h-20' />
              ))}
            </div>
          ) : overdueOrders && overdueOrders.length > 0 ? (
            <div className='flex flex-col gap-2'>
              {overdueOrders.map((order) => (
                <RepairOrderItem key={order.id} order={order} />
              ))}
            </div>
          ) : (
            <p className='text-center text-sm text-gray-500'>No overdue orders</p>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className='flex items-center gap-2 text-lg'>
            <svg
              xmlns='http://www.w3.org/2000/svg'
              viewBox='0 0 24 24'
              fill='none'
              stroke='currentColor'
              strokeLinecap='round'
              strokeLinejoin='round'
              strokeWidth='2'
              className='h-5 w-5 text-blue-500'
            >
              <rect x='3' y='4' width='18' height='18' rx='2' ry='2' />
              <line x1='16' y1='2' x2='16' y2='6' />
              <line x1='8' y1='2' x2='8' y2='6' />
              <line x1='3' y1='10' x2='21' y2='10' />
            </svg>
            Top 5 Recent
          </CardTitle>
        </CardHeader>
        <CardContent>
          {recentLoading ? (
            <div className='flex flex-col gap-3'>
              {[1, 2, 3].map((i) => (
                <Skeleton key={i} className='h-20' />
              ))}
            </div>
          ) : recentOrders && recentOrders.length > 0 ? (
            <div className='flex flex-col gap-2'>
              {recentOrders.map((order) => (
                <RepairOrderItem key={order.id} order={order} />
              ))}
            </div>
          ) : (
            <p className='text-center text-sm text-gray-500'>No recent orders</p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
