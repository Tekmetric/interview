import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'

import { createRepairOrderSchema } from '@shared/validation'
import type { CreateRepairOrderInput } from '@shared/validation'
import { PRIORITY } from '@shared/constants'
import { getItem, setItem, removeItem } from '@/lib/storage'

import { RO_FORM_DEFAULTS } from '../ro-constants'

const STORAGE_KEY = 'ro-create-draft'

export function useRepairOrderForm(defaultValues?: Partial<CreateRepairOrderInput>) {
  const currentYear = new Date().getFullYear()

  const savedDraft = getItem<Partial<CreateRepairOrderInput>>(STORAGE_KEY, {})

  const form = useForm<CreateRepairOrderInput>({
    resolver: zodResolver(createRepairOrderSchema) as any,
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
      priority: PRIORITY.NORMAL,
      estimatedDuration: RO_FORM_DEFAULTS.ESTIMATED_DURATION,
      estimatedCost: RO_FORM_DEFAULTS.ESTIMATED_COST,
      dueTime: undefined,
      notes: '',
      ...savedDraft,
      ...defaultValues,
    },
  })

  const watchedValues = form.watch()

  useEffect(() => {
    setItem(STORAGE_KEY, watchedValues)
  }, [watchedValues])

  return form
}

export function clearRODraft() {
  removeItem(STORAGE_KEY)
}
