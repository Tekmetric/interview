import { useSuspenseQuery } from '@tanstack/react-query'
import type { Technician } from '@shared/types'
import { API_ENDPOINTS } from '@shared/constants'

async function fetchTechnicians(): Promise<Technician[]> {
  const response = await fetch(API_ENDPOINTS.TECHNICIANS.BASE)
  if (!response.ok) {
    throw new Error('Failed to fetch technicians')
  }
  return response.json()
}

export function useTechnicians() {
  return useSuspenseQuery({
    queryKey: ['technicians'],
    queryFn: fetchTechnicians,
  })
}
