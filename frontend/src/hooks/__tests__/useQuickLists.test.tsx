import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useOverdueOrders, useRecentOrders } from '../useQuickLists'
import type { RepairOrder } from '@shared/types'
import type { ReactNode } from 'react'

const mockOrders: RepairOrder[] = [
  {
    id: 'RO-1',
    status: 'IN_PROGRESS',
    customer: {
      name: 'Test Customer',
      phone: '555-0000',
    },
    vehicle: {
      year: 2020,
      make: 'Toyota',
      model: 'Camry',
    },
    services: ['Oil Change'],
    assignedTech: null,
    priority: 'NORMAL',
    estimatedDuration: null,
    estimatedCost: null,
    dueTime: '2024-01-10T12:00:00Z',
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

function createWrapper() {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })
  const Wrapper = ({ children }: { children: ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  )
  Wrapper.displayName = 'TestWrapper'
  return Wrapper
}

describe('useOverdueOrders', () => {
  beforeEach(() => {
    vi.stubGlobal(
      'fetch',
      vi.fn(() =>
        Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockOrders),
        }),
      ),
    )
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('should fetch overdue orders with default limit', async () => {
    const fetchSpy = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockOrders),
      }),
    )
    vi.stubGlobal('fetch', fetchSpy)

    const { result } = renderHook(() => useOverdueOrders(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(fetchSpy).toHaveBeenCalledWith('/api/repairOrders/overdue?limit=5')
    expect(result.current.data).toEqual(mockOrders)
  })

  it('should fetch overdue orders with custom limit', async () => {
    const fetchSpy = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockOrders),
      }),
    )
    vi.stubGlobal('fetch', fetchSpy)

    const { result } = renderHook(() => useOverdueOrders(10), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(fetchSpy).toHaveBeenCalledWith('/api/repairOrders/overdue?limit=10')
  })

  it('should handle fetch errors', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(() =>
        Promise.resolve({
          ok: false,
        }),
      ),
    )

    const { result } = renderHook(() => useOverdueOrders(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isError).toBe(true))
  })
})

describe('useRecentOrders', () => {
  beforeEach(() => {
    vi.stubGlobal(
      'fetch',
      vi.fn(() =>
        Promise.resolve({
          ok: true,
          json: () => Promise.resolve(mockOrders),
        }),
      ),
    )
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('should fetch recent orders with default limit', async () => {
    const fetchSpy = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockOrders),
      }),
    )
    vi.stubGlobal('fetch', fetchSpy)

    const { result } = renderHook(() => useRecentOrders(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(fetchSpy).toHaveBeenCalledWith('/api/repairOrders/recent?limit=5')
    expect(result.current.data).toEqual(mockOrders)
  })

  it('should fetch recent orders with custom limit', async () => {
    const fetchSpy = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockOrders),
      }),
    )
    vi.stubGlobal('fetch', fetchSpy)

    const { result } = renderHook(() => useRecentOrders(10), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(fetchSpy).toHaveBeenCalledWith('/api/repairOrders/recent?limit=10')
  })

  it('should handle fetch errors', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(() =>
        Promise.resolve({
          ok: false,
        }),
      ),
    )

    const { result } = renderHook(() => useRecentOrders(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isError).toBe(true))
  })
})
