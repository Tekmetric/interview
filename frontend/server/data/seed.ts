import { faker } from '@faker-js/faker'
import type { Technician, RepairOrderStatus } from '../../shared/types.js'
import { insertTechnician } from '../domains/technicians/repository.js'
import {
  insertRepairOrderDirect,
  getAllRepairOrders,
} from '../domains/repair-orders/repository.js'
import { getAllTechnicians } from '../domains/technicians/repository.js'

const TECHNICIANS: Technician[] = [
  {
    id: 'TECH-001',
    name: 'Mike Johnson',
    initials: 'MJ',
    specialties: ['Engine', 'Transmission'],
    active: true,
  },
  {
    id: 'TECH-002',
    name: 'Sarah Williams',
    initials: 'SW',
    specialties: ['Brakes', 'Suspension'],
    active: true,
  },
  {
    id: 'TECH-003',
    name: 'John Davis',
    initials: 'JD',
    specialties: ['Electrical', 'A/C'],
    active: true,
  },
  {
    id: 'TECH-004',
    name: 'Emily Chen',
    initials: 'EC',
    specialties: ['Engine', 'Diagnostics'],
    active: true,
  },
]

const MAKES = [
  'Honda',
  'Toyota',
  'Ford',
  'Chevrolet',
  'BMW',
  'Tesla',
  'Mazda',
  'Nissan',
  'Hyundai',
  'Volkswagen',
]
const MODELS: Record<string, string[]> = {
  Honda: ['Accord', 'Civic', 'CR-V', 'Pilot'],
  Toyota: ['Camry', 'Corolla', 'RAV4', 'Tacoma'],
  Ford: ['F-150', 'Mustang', 'Explorer', 'Escape'],
  Chevrolet: ['Silverado', 'Malibu', 'Equinox', 'Tahoe'],
  BMW: ['3 Series', '5 Series', 'X3', 'X5'],
  Tesla: ['Model 3', 'Model Y', 'Model S', 'Model X'],
  Mazda: ['CX-5', 'Mazda3', 'CX-9', 'MX-5'],
  Nissan: ['Altima', 'Rogue', 'Sentra', 'Pathfinder'],
  Hyundai: ['Sonata', 'Tucson', 'Elantra', 'Santa Fe'],
  Volkswagen: ['Jetta', 'Tiguan', 'Passat', 'Atlas'],
}

const SERVICES = [
  'Oil Change',
  'Brake Inspection',
  'Tire Rotation',
  'Engine Diagnostic',
  'Transmission Service',
  'A/C Recharge',
  'Battery Replacement',
  'Wheel Alignment',
  'Coolant Flush',
  'Spark Plug Replacement',
  'Air Filter Replacement',
  'Brake Pad Replacement',
  'Suspension Repair',
  'Exhaust Repair',
]

function generateVIN(): string {
  const chars = 'ABCDEFGHJKLMNPRSTUVWXYZ0123456789'
  return Array.from(
    { length: 17 },
    () => chars[Math.floor(Math.random() * chars.length)],
  ).join('')
}

function generatePlate(): string {
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
  const numbers = '0123456789'
  return `${letters[Math.floor(Math.random() * 26)]}${letters[Math.floor(Math.random() * 26)]}${letters[Math.floor(Math.random() * 26)]}-${numbers[Math.floor(Math.random() * 10)]}${numbers[Math.floor(Math.random() * 10)]}${numbers[Math.floor(Math.random() * 10)]}`
}

function generateRepairOrder(index: number) {
  const make = faker.helpers.arrayElement(MAKES)
  const model = faker.helpers.arrayElement(MODELS[make])
  const year = faker.number.int({ min: 2015, max: 2024 })

  // Status distribution: more in early stages
  const statusWeights = [
    { value: 'NEW', weight: 3 },
    { value: 'AWAITING_APPROVAL', weight: 4 },
    { value: 'IN_PROGRESS', weight: 5 },
    { value: 'WAITING_PARTS', weight: 2 },
    { value: 'COMPLETED', weight: 1 },
  ]
  const status = faker.helpers.weightedArrayElement(statusWeights) as RepairOrderStatus

  // 12% chance of overdue
  const isOverdue = Math.random() < 0.12
  const dueTime = isOverdue
    ? faker.date.past({ refDate: new Date() }).toISOString()
    : faker.date.soon({ refDate: new Date() }).toISOString()

  // Assign tech if not NEW
  const techId = status !== 'NEW' ? faker.helpers.arrayElement(TECHNICIANS).id : null

  // Approved by customer if COMPLETED
  const approvedByCustomer = status === 'COMPLETED' ? 1 : 0

  const services = faker.helpers.arrayElements(SERVICES, { min: 1, max: 4 })
  const priority = Math.random() < 0.3 ? 'HIGH' : 'NORMAL'

  return {
    id: `RO-${String(index + 1).padStart(4, '0')}`,
    status,
    customer_name: faker.person.fullName(),
    customer_phone: faker.phone.number(),
    customer_email: faker.internet.email(),
    vehicle_year: year,
    vehicle_make: make,
    vehicle_model: model,
    vehicle_trim: faker.helpers.arrayElement(['Base', 'LX', 'EX', 'Limited', 'Premium']),
    vehicle_vin: generateVIN(),
    vehicle_plate: generatePlate(),
    vehicle_mileage: faker.number.int({ min: 10000, max: 150000 }),
    vehicle_color: faker.vehicle.color(),
    services: JSON.stringify(services),
    technician_id: techId,
    priority,
    estimated_duration: faker.number.int({ min: 30, max: 240 }),
    estimated_cost: faker.number.int({ min: 50, max: 800 }),
    due_time: dueTime,
    notes: Math.random() > 0.7 ? faker.lorem.sentence() : '',
    approved_by_customer: approvedByCustomer,
    created_at: faker.date.recent({ refDate: new Date() }).toISOString(),
  }
}

function seedDatabase() {
  console.log('🌱 Seeding database...')

  // Check if already seeded
  const existingOrders = getAllRepairOrders()
  const existingTechs = getAllTechnicians()

  if (existingOrders.length > 0 || existingTechs.length > 0) {
    console.log('⚠️  Database already has data. Skipping seed.')
    console.log(`   - ${existingTechs.length} technicians`)
    console.log(`   - ${existingOrders.length} repair orders`)
    return
  }

  // Insert technicians
  console.log('👷 Inserting technicians...')
  TECHNICIANS.forEach((tech) => insertTechnician(tech))

  // Insert repair orders
  console.log('🔧 Inserting 50 repair orders...')
  for (let i = 0; i < 50; i++) {
    const order = generateRepairOrder(i)
    insertRepairOrderDirect(order)
  }

  console.log('✅ Database seeded successfully!')
  console.log(`   - ${TECHNICIANS.length} technicians`)
  console.log(`   - 50 repair orders`)

  // Show distribution
  const orders = getAllRepairOrders()
  const statusCounts: Record<string, number> = {}
  orders.forEach((order) => {
    statusCounts[order.status] = (statusCounts[order.status] || 0) + 1
  })

  console.log('\n📊 Status distribution:')
  Object.entries(statusCounts).forEach(([status, count]) => {
    console.log(`   ${status}: ${count}`)
  })
}

seedDatabase()
