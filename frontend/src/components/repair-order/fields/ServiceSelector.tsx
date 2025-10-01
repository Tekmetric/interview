import { useState } from 'react'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { X } from 'lucide-react'
import type { UseFormSetValue, FieldErrors } from 'react-hook-form'
import type { CreateRepairOrderInput } from '@shared/validation'

type ServiceSelectorProps = {
  services: string[]
  setValue: UseFormSetValue<CreateRepairOrderInput>
  errors: FieldErrors<CreateRepairOrderInput>
}

export function ServiceSelector({ services, setValue, errors }: ServiceSelectorProps) {
  const [inputValue, setInputValue] = useState('')

  const handleAddService = () => {
    const trimmed = inputValue.trim()
    if (trimmed && !services.includes(trimmed)) {
      setValue('services', [...services, trimmed], { shouldValidate: true })
      setInputValue('')
    }
  }

  const handleRemoveService = (index: number) => {
    setValue(
      'services',
      services.filter((_, i) => i !== index),
      { shouldValidate: true },
    )
  }

  const handleKeyDown = (e: { key: string; preventDefault: () => void }) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      handleAddService()
    }
  }

  return (
    <div className='space-y-3'>
      <Label htmlFor='services'>
        Services <span className='text-red-500'>*</span>
      </Label>

      <div className='flex gap-2'>
        <Input
          id='services'
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder='Type service and press Enter'
        />
        <Button type='button' onClick={handleAddService} variant='outline'>
          Add
        </Button>
      </div>

      {services.length > 0 && (
        <div className='flex flex-wrap gap-2'>
          {services.map((service, index) => (
            <Badge key={index} variant='secondary' className='gap-1 pr-1'>
              {service}
              <button
                type='button'
                onClick={() => handleRemoveService(index)}
                className='rounded-sm hover:bg-black/10'
              >
                <X className='h-3 w-3' />
              </button>
            </Badge>
          ))}
        </div>
      )}

      {errors.services && (
        <p className='text-xs text-red-600'>{errors.services.message}</p>
      )}
    </div>
  )
}
