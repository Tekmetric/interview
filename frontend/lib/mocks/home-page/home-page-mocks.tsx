import type { components } from '@/lib/api/contracts/generated/api.types'
import { BarChart3, Car, CircleDollarSign, Users, Wrench } from 'lucide-react'

type RepairOrderStatus = components['schemas']['RepairOrder']['status']

export const navItems = [
  { href: '/dashboard', label: 'Dashboard' },
  { href: '/customers', label: 'Customers' },
  { href: '/vehicles', label: 'Vehicles' },
  { href: '/repair-orders', label: 'Repair Orders' },
]

export const mockKpis = [
  {
    label: 'Cars Today',
    value: '24',
    iconClass: 'bg-sky-900/60 text-sky-300',
    valueClass: 'text-white',
    icon: <Car className="h-5 w-5" />,
  },
  {
    label: 'Revenue Today',
    value: '$15,840',
    iconClass: 'bg-emerald-900/60 text-emerald-300',
    valueClass: 'text-emerald-400',
    icon: <CircleDollarSign className="h-5 w-5" />,
  },
  {
    label: 'Average RO',
    value: '$660',
    iconClass: 'bg-amber-900/50 text-amber-300',
    valueClass: 'text-white',
    icon: <BarChart3 className="h-5 w-5" />,
  },
  {
    label: 'Open ROs',
    value: '8',
    iconClass: 'bg-brand text-white',
    valueClass: 'text-brand',
    icon: <Wrench className="h-5 w-5" />,
  },
]

export const mockOrders: Array<{
  id: string
  status: RepairOrderStatus
  vehicle: string
  total: string
}> = [
  {
    id: 'RO-4A2B',
    status: 'in_progress',
    vehicle: '2019 Toyota Camry',
    total: '$1,248.50',
  },
  {
    id: 'RO-8F91',
    status: 'completed',
    vehicle: '2021 Ford F-150',
    total: '$874.00',
  },
  {
    id: 'RO-C37D',
    status: 'waiting_parts',
    vehicle: '2017 Honda Accord',
    total: '$2,105.75',
  },
]

export const features = [
  {
    title: 'Repair Order Management',
    description:
      'Create, track, and close repair orders with real-time status updates. Labor, parts, tax, and totals calculated automatically.',
    href: '/repair-orders',
    colorClass: 'bg-brand-50 text-brand-700 border-brand-200',
    icon: <Wrench className="h-6 w-6" />,
    stat: '24 ROs today',
  },
  {
    title: 'Customer Records',
    description:
      "Full customer history at your fingertips. Phone, email, and every vehicle they've ever brought in - one click away.",
    href: '/customers',
    colorClass: 'bg-sky-50 text-sky-700 border-sky-200',
    icon: <Users className="h-6 w-6" />,
    stat: '1,200+ customers',
  },
  {
    title: 'Vehicle History',
    description:
      'VIN, mileage, year, make, and model - all stored. See every service performed on a vehicle across its entire lifetime.',
    href: '/vehicles',
    colorClass: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    icon: <Car className="h-6 w-6" />,
    stat: '3,400+ vehicles',
  },
]
