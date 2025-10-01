import { useSuspenseQuery } from '@tanstack/react-query'
import type { Technician } from '@shared/types'

async function fetchTechnicians(): Promise<Technician[]> {
  const response = await fetch('/api/technicians')
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
