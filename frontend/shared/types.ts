export type RepairOrderStatus =
  | 'NEW'
  | 'AWAITING_APPROVAL'
  | 'IN_PROGRESS'
  | 'WAITING_PARTS'
  | 'COMPLETED'

export type Priority = 'HIGH' | 'NORMAL'

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
