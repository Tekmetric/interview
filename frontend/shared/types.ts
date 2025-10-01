import type { REPAIR_ORDER_STATUSES, PRIORITIES } from './constants'

export type RepairOrderStatus = (typeof REPAIR_ORDER_STATUSES)[number]

export type Priority = (typeof PRIORITIES)[number]

export interface Technician {
  id: string
  name: string
  initials: string
  specialties: string[]
  active: boolean
}

export interface RepairOrder {
  id: string
  status: RepairOrderStatus
  customer: {
    name: string
    phone: string
    email?: string
  }
  vehicle: {
    year: number
    make: string
    model: string
    trim?: string
    vin?: string
    plate?: string
    mileage?: number
    color?: string
  }
  services: string[]
  assignedTech: Technician | null
  priority: Priority
  estimatedDuration?: number
  estimatedCost?: number
  dueTime?: string
  notes: string
  approvedByCustomer: boolean
  createdAt: string
  updatedAt: string
}
