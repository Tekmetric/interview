import { describe, it, expect, vi, type Mock } from 'vitest'
import { render, screen, waitFor } from '@/test/test-utils'
import { RODetailsForm } from '../ro-details-form'
import type { RepairOrder } from '@shared/types'
import { useTechnicians } from '@/components/technician/hooks/useTechnicians'

vi.mock('@/components/technician/hooks/useTechnicians')

const mockOrder: RepairOrder = {
  id: 'RO-123',
  status: 'IN_PROGRESS',
  customer: {
    name: 'John Doe',
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
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
  notes: '',
  approvedByCustomer: false,
}

describe('RODetailsForm', () => {
  it('should render read-only fields', async () => {
    ;(useTechnicians as Mock).mockReturnValue({ data: [] })
    render(
      <RODetailsForm
        order={mockOrder}
        onSubmit={vi.fn()}
        onCancel={vi.fn()}
        onDelete={vi.fn()}
      />,
    )

    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument()
      expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument()
      expect(screen.getByText('Oil Change')).toBeInTheDocument()
    })
  })
})
