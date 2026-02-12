import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { QuickLists } from '../quick-lists'
import type { RepairOrder } from '@shared/types'
import type { ReactNode } from 'react'

const mockOverdueOrders: RepairOrder[] = [
  {
    id: 'RO-1',
    status: 'IN_PROGRESS',
    customer: {
      name: 'Overdue Customer',
      phone: '555-0001',
    },
    vehicle: {
      year: 2020,
      make: 'Toyota',
      model: 'Camry',
    },
    services: ['Oil Change'],
    assignedTech: null,
    priority: 'HIGH',
    estimatedDuration: undefined,
    estimatedCost: undefined,
    dueTime: '2024-01-10T12:00:00Z',
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
]

const mockRecentOrders: RepairOrder[] = [
  {
    id: 'RO-2',
    status: 'NEW',
    customer: {
      name: 'Recent Customer',
      phone: '555-0002',
    },
    vehicle: {
      year: 2021,
      make: 'Honda',
      model: 'Civic',
    },
    services: ['Brake Service'],
    assignedTech: null,
    priority: 'NORMAL',
    estimatedDuration: undefined,
    estimatedCost: undefined,
    dueTime: undefined,
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-15T00:00:00Z',
    updatedAt: '2024-01-15T00:00:00Z',
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

describe('QuickLists', () => {
  it('should render both list titles', () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url.includes('overdue')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve([]),
          })
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        })
      }),
    )

    const Wrapper = createWrapper()
    render(
      <Wrapper>
        <QuickLists />
      </Wrapper>,
    )

    expect(screen.getByText('Top 5 Overdue')).toBeInTheDocument()
    expect(screen.getByText('Top 5 Recent')).toBeInTheDocument()

    vi.unstubAllGlobals()
  })

  it('should render overdue orders when data is available', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url.includes('overdue')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve(mockOverdueOrders),
          })
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        })
      }),
    )

    const Wrapper = createWrapper()
    render(
      <Wrapper>
        <QuickLists />
      </Wrapper>,
    )

    expect(await screen.findByText('RO-1')).toBeInTheDocument()
    expect(await screen.findByText('Overdue Customer')).toBeInTheDocument()

    vi.unstubAllGlobals()
  })

  it('should render recent orders when data is available', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url.includes('recent')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve(mockRecentOrders),
          })
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        })
      }),
    )

    const Wrapper = createWrapper()
    render(
      <Wrapper>
        <QuickLists />
      </Wrapper>,
    )

    expect(await screen.findByText('RO-2')).toBeInTheDocument()
    expect(await screen.findByText('Recent Customer')).toBeInTheDocument()

    vi.unstubAllGlobals()
  })

  it('should show empty state when no overdue orders', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url.includes('overdue')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve([]),
          })
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        })
      }),
    )

    const Wrapper = createWrapper()
    render(
      <Wrapper>
        <QuickLists />
      </Wrapper>,
    )

    expect(await screen.findByText('No overdue orders')).toBeInTheDocument()

    vi.unstubAllGlobals()
  })

  it('should show empty state when no recent orders', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url.includes('recent')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve([]),
          })
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        })
      }),
    )

    const Wrapper = createWrapper()
    render(
      <Wrapper>
        <QuickLists />
      </Wrapper>,
    )

    expect(await screen.findByText('No recent orders')).toBeInTheDocument()

    vi.unstubAllGlobals()
  })

  it('should render both lists with data', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn((url: string) => {
        if (url.includes('overdue')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve(mockOverdueOrders),
          })
        }
        if (url.includes('recent')) {
          return Promise.resolve({
            ok: true,
            json: () => Promise.resolve(mockRecentOrders),
          })
        }
        return Promise.resolve({
          ok: true,
          json: () => Promise.resolve([]),
        })
      }),
    )

    const Wrapper = createWrapper()
    render(
      <Wrapper>
        <QuickLists />
      </Wrapper>,
    )

    expect(await screen.findByText('RO-1')).toBeInTheDocument()
    expect(await screen.findByText('RO-2')).toBeInTheDocument()
    expect(await screen.findByText('Overdue Customer')).toBeInTheDocument()
    expect(await screen.findByText('Recent Customer')).toBeInTheDocument()

    vi.unstubAllGlobals()
  })
})
