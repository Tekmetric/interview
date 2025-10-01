import { describe, it, expect, vi } from 'vitest'
import { calculateKPIs } from '../kpi-utils'
import type { RepairOrder } from '@shared/types'

describe('calculateKPIs', () => {
  const mockDate = new Date('2024-01-15T12:00:00Z')

  const createMockOrder = (overrides: Partial<RepairOrder>): RepairOrder => ({
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
    estimatedDuration: undefined,
    estimatedCost: undefined,
    dueTime: undefined,
    notes: '',
    approvedByCustomer: false,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    ...overrides,
  })

  it('should calculate totalWIP (non-completed orders)', () => {
    const orders: RepairOrder[] = [
      createMockOrder({ status: 'NEW' }),
      createMockOrder({ status: 'IN_PROGRESS' }),
      createMockOrder({ status: 'COMPLETED' }),
      createMockOrder({ status: 'WAITING_PARTS' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis.totalWIP).toBe(3)
  })

  it('should calculate overdueCount for past due orders', () => {
    const orders: RepairOrder[] = [
      createMockOrder({ dueTime: '2024-01-14T12:00:00Z', status: 'IN_PROGRESS' }),
      createMockOrder({ dueTime: '2024-01-16T12:00:00Z', status: 'IN_PROGRESS' }),
      createMockOrder({ dueTime: '2024-01-14T12:00:00Z', status: 'COMPLETED' }),
      createMockOrder({ dueTime: undefined, status: 'IN_PROGRESS' }),
    ]

    // Mock current time
    vi.useFakeTimers()
    vi.setSystemTime(mockDate)

    const kpis = calculateKPIs(orders)
    expect(kpis.overdueCount).toBe(1)

    vi.useRealTimers()
  })

  it('should calculate waitingPartsCount', () => {
    const orders: RepairOrder[] = [
      createMockOrder({ status: 'WAITING_PARTS' }),
      createMockOrder({ status: 'WAITING_PARTS' }),
      createMockOrder({ status: 'IN_PROGRESS' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis.waitingPartsCount).toBe(2)
  })

  it('should calculate awaitingApprovalCount', () => {
    const orders: RepairOrder[] = [
      createMockOrder({ status: 'AWAITING_APPROVAL' }),
      createMockOrder({ status: 'AWAITING_APPROVAL' }),
      createMockOrder({ status: 'AWAITING_APPROVAL' }),
      createMockOrder({ status: 'IN_PROGRESS' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis.awaitingApprovalCount).toBe(3)
  })

  it('should handle empty array', () => {
    const kpis = calculateKPIs([])
    expect(kpis).toEqual({
      totalWIP: 0,
      overdueCount: 0,
      waitingPartsCount: 0,
      awaitingApprovalCount: 0,
    })
  })

  it('should handle all completed orders', () => {
    const orders: RepairOrder[] = [
      createMockOrder({ status: 'COMPLETED' }),
      createMockOrder({ status: 'COMPLETED' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis).toEqual({
      totalWIP: 0,
      overdueCount: 0,
      waitingPartsCount: 0,
      awaitingApprovalCount: 0,
    })
  })

  it('should not count completed orders as overdue even if past due time', () => {
    vi.useFakeTimers()
    vi.setSystemTime(mockDate)

    const orders: RepairOrder[] = [
      createMockOrder({ dueTime: '2024-01-14T12:00:00Z', status: 'COMPLETED' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis.overdueCount).toBe(0)

    vi.useRealTimers()
  })

  it('should handle orders with no due time', () => {
    const orders: RepairOrder[] = [
      createMockOrder({ dueTime: undefined, status: 'IN_PROGRESS' }),
      createMockOrder({ dueTime: undefined, status: 'NEW' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis.overdueCount).toBe(0)
  })

  it('should calculate all metrics correctly with mixed orders', () => {
    vi.useFakeTimers()
    vi.setSystemTime(mockDate)

    const orders: RepairOrder[] = [
      createMockOrder({ status: 'NEW' }),
      createMockOrder({ status: 'IN_PROGRESS', dueTime: '2024-01-14T12:00:00Z' }),
      createMockOrder({ status: 'WAITING_PARTS' }),
      createMockOrder({ status: 'AWAITING_APPROVAL' }),
      createMockOrder({ status: 'AWAITING_APPROVAL' }),
      createMockOrder({ status: 'COMPLETED' }),
    ]

    const kpis = calculateKPIs(orders)
    expect(kpis).toEqual({
      totalWIP: 5,
      overdueCount: 1,
      waitingPartsCount: 1,
      awaitingApprovalCount: 2,
    })

    vi.useRealTimers()
  })
})
