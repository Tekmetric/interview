import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { useRepairOrders } from '../useRepairOrders'
import type { RepairOrder } from '@shared/types'
import type { ReactNode } from 'react'

const mockOrders: RepairOrder[] = [
  {
    id: 'RO-1',
    status: 'NEW',
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
    dueTime: null,
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

describe('useRepairOrders', () => {
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

  it('should fetch repair orders successfully', async () => {
    const { result } = renderHook(() => useRepairOrders(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toEqual(mockOrders)
  })

  it('should call fetch with correct URL', async () => {
    const fetchSpy = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockOrders),
      }),
    )
    vi.stubGlobal('fetch', fetchSpy)

    renderHook(() => useRepairOrders(), {
      wrapper: createWrapper(),
    })

    await waitFor(() => expect(fetchSpy).toHaveBeenCalledWith('/api/repairOrders'))
  })

  // Note: useSuspenseQuery throws errors which require Suspense boundaries
  // Error handling tests are omitted as they would need Suspense + ErrorBoundary setup
})
