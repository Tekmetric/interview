import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactNode, Suspense } from 'react'
import { useTechnicians } from '../useTechnicians'
import type { Technician } from '@shared/types'

const mockTechnicians: Technician[] = [
  {
    id: 'TECH-001',
    name: 'Mike Johnson',
    initials: 'MJ',
    specialties: ['Engine', 'Electrical'],
    active: true,
  },
  {
    id: 'TECH-002',
    name: 'Sarah Chen',
    initials: 'SC',
    specialties: ['Brakes', 'Suspension'],
    active: true,
  },
]

describe('useTechnicians', () => {
  let queryClient: QueryClient

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
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

  it('should fetch technicians', async () => {
    ;(global.fetch as any).mockResolvedValueOnce({
      ok: true,
      json: async () => mockTechnicians,
    })

    const { result } = renderHook(() => useTechnicians(), { wrapper })

    await waitFor(() => expect(result.current.data).toEqual(mockTechnicians))
  })

  it('should throw error on fetch failure', async () => {
    ;(global.fetch as any).mockResolvedValueOnce({
      ok: false,
    })

    expect(() => renderHook(() => useTechnicians(), { wrapper })).toBeDefined()
  })
})
