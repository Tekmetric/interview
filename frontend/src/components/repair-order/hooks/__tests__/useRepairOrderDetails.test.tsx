import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactNode, Suspense } from 'react'
import {
  useRepairOrder,
  useUpdateRepairOrder,
  useDeleteRepairOrder,
} from '../useRepairOrderDetails'
import type { RepairOrder } from '@shared/types'

const mockOrder: RepairOrder = {
  id: 'RO-001',
  status: 'IN_PROGRESS',
  customer: {
    name: 'John Doe',
    phone: '555-0100',
    email: 'john@example.com',
  },
  vehicle: {
    year: 2020,
    make: 'Toyota',
    model: 'Camry',
    mileage: 50000,
  },
  services: ['Oil Change', 'Tire Rotation'],
  assignedTech: {
    id: 'TECH-001',
    name: 'Mike Johnson',
    initials: 'MJ',
    specialties: ['Engine', 'Electrical'],
    active: true,
  },
  priority: 'NORMAL',
  notes: 'Test note',
  approvedByCustomer: true,
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
}

describe('useRepairOrderDetails hooks', () => {
  let queryClient: QueryClient

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    })
    global.fetch = vi.fn()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  const wrapper = ({ children }: { children: ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      <Suspense fallback={<div>Loading...</div>}>{children}</Suspense>
    </QueryClientProvider>
  )

  describe('useRepairOrder', () => {
    it('should fetch repair order by id', async () => {
      ;(global.fetch as any).mockResolvedValueOnce({
        ok: true,
        json: async () => mockOrder,
      })

      const { result } = renderHook(() => useRepairOrder('RO-001'), { wrapper })

      await waitFor(() => expect(result.current.data).toEqual(mockOrder))
    })

    it('should throw error on fetch failure', async () => {
      ;(global.fetch as any).mockResolvedValueOnce({
        ok: false,
      })

      expect(() => renderHook(() => useRepairOrder('RO-001'), { wrapper })).toBeDefined()
    })
  })

  describe('useUpdateRepairOrder', () => {
    it('should update repair order', async () => {
      const updatedOrder = { ...mockOrder, notes: 'Updated note' }
      ;(global.fetch as any).mockResolvedValueOnce({
        ok: true,
        json: async () => updatedOrder,
      })

      const { result } = renderHook(() => useUpdateRepairOrder(), { wrapper })

      result.current.mutate({
        id: 'RO-001',
        data: { notes: 'Updated note', dueTime: undefined },
      })

      await waitFor(() => expect(result.current.isSuccess).toBe(true))
    })

    it('should handle update error', async () => {
      ;(global.fetch as any).mockResolvedValueOnce({
        ok: false,
        json: async () => ({ message: 'Update failed' }),
      })

      const { result } = renderHook(() => useUpdateRepairOrder(), { wrapper })

      result.current.mutate({
        id: 'RO-001',
        data: { notes: 'Updated note', dueTime: undefined },
      })

      await waitFor(() => expect(result.current.isError).toBe(true))
    })
  })

  describe('useDeleteRepairOrder', () => {
    it('should delete repair order', async () => {
      ;(global.fetch as any).mockResolvedValueOnce({
        ok: true,
      })

      const { result } = renderHook(() => useDeleteRepairOrder(), { wrapper })

      result.current.mutate('RO-001')

      await waitFor(() => expect(result.current.isSuccess).toBe(true))
    })

    it('should handle delete error', async () => {
      ;(global.fetch as any).mockResolvedValueOnce({
        ok: false,
      })

      const { result } = renderHook(() => useDeleteRepairOrder(), { wrapper })

      result.current.mutate('RO-001')

      await waitFor(() => expect(result.current.isError).toBe(true))
    })
  })
})
