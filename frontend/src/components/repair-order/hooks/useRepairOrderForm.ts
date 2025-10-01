import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { createRepairOrderSchema } from '@shared/validation'
import type { CreateRepairOrderInput } from '@shared/validation'

export function useRepairOrderForm(defaultValues?: Partial<CreateRepairOrderInput>) {
  const currentYear = new Date().getFullYear()

  return useForm<CreateRepairOrderInput>({
    resolver: zodResolver(createRepairOrderSchema),
    defaultValues: {
      customer: {
        name: '',
        phone: '',
        email: '',
      },
      vehicle: {
        year: currentYear,
        make: '',
        model: '',
        trim: '',
        vin: '',
        plate: '',
        mileage: undefined,
        color: '',
      },
      services: [],
      priority: 'NORMAL',
      estimatedDuration: undefined,
      estimatedCost: undefined,
      dueTime: undefined,
      notes: '',
      ...defaultValues,
    },
  })
}
