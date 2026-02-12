import { useState, useRef, useEffect, type ChangeEvent, type KeyboardEvent } from 'react'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { X } from 'lucide-react'
import type { UseFormSetValue, FieldErrors } from 'react-hook-form'
import type { CreateRepairOrderInput } from '@shared/validation'
import { COMMON_SERVICES } from '../ro-constants'

type ServiceSelectorProps = {
  services: string[]
  setValue: UseFormSetValue<CreateRepairOrderInput>
  errors: FieldErrors<CreateRepairOrderInput>
}

export function ServiceSelector({ services, setValue, errors }: ServiceSelectorProps) {
  const [inputValue, setInputValue] = useState('')
  const [showSuggestions, setShowSuggestions] = useState(false)
  const [selectedIndex, setSelectedIndex] = useState(-1)

  const inputRef = useRef<HTMLInputElement>(null)
  const suggestionsRef = useRef<HTMLDivElement>(null)

  const filteredSuggestions = COMMON_SERVICES.filter(
    (service) =>
      service.toLowerCase().includes(inputValue.toLowerCase()) &&
      !services.includes(service),
  ).slice(0, 5)

  useEffect(() => {
    const handleClickOutside = (e: globalThis.MouseEvent) => {
      if (
        suggestionsRef.current &&
        !suggestionsRef.current.contains(e.target as Node) &&
        inputRef.current &&
        !inputRef.current.contains(e.target as Node)
      ) {
        setShowSuggestions(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const handleAddService = (service?: string) => {
    const serviceToAdd = service || inputValue.trim()
    if (serviceToAdd && !services.includes(serviceToAdd)) {
      setValue('services', [...services, serviceToAdd], { shouldValidate: true })
      setInputValue('')
      setShowSuggestions(false)
      setSelectedIndex(-1)
    }
  }

  const handleRemoveService = (index: number) => {
    setValue(
      'services',
      services.filter((_, i) => i !== index),
      { shouldValidate: true },
    )
  }

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault()
      if (selectedIndex >= 0 && filteredSuggestions[selectedIndex]) {
        handleAddService(filteredSuggestions[selectedIndex])
      } else {
        handleAddService()
      }
    } else if (e.key === 'ArrowDown') {
      e.preventDefault()
      setSelectedIndex((prev) =>
        prev < filteredSuggestions.length - 1 ? prev + 1 : prev,
      )
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1))
    } else if (e.key === 'Escape') {
      setShowSuggestions(false)
      setSelectedIndex(-1)
    }
  }

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value)
    setShowSuggestions(true)
    setSelectedIndex(-1)
  }

  return (
    <div className='space-y-3'>
      <div className='flex flex-wrap items-center gap-2'>
        <Label htmlFor='services'>
          Services <span className='text-red-500'>*</span>
        </Label>
        {services.length > 0 &&
          services.map((service, index) => (
            <Badge
              key={index}
              className='gap-1.5 border-blue-200 bg-blue-50 pr-1.5 text-blue-700 hover:bg-blue-100'
            >
              {service}
              <button
                type='button'
                onClick={() => handleRemoveService(index)}
                className='rounded-sm hover:bg-blue-200/50'
              >
                <X className='h-3 w-3' />
              </button>
            </Badge>
          ))}
      </div>

      <div className='relative flex gap-2'>
        <div className='relative flex-1'>
          <Input
            ref={inputRef}
            id='services'
            value={inputValue}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            onFocus={() => setShowSuggestions(true)}
            placeholder='Type service and press Enter'
            autoComplete='off'
          />
          {showSuggestions && inputValue && filteredSuggestions.length > 0 && (
            <div
              ref={suggestionsRef}
              className='absolute top-full z-50 mt-1 w-full rounded-md border bg-white shadow-lg'
            >
              {filteredSuggestions.map((suggestion, index) => (
                <button
                  key={suggestion}
                  type='button'
                  onClick={() => handleAddService(suggestion)}
                  className={`w-full px-3 py-2 text-left text-sm hover:bg-gray-100 ${
                    index === selectedIndex ? 'bg-gray-100' : ''
                  } ${index === 0 ? 'rounded-t-md' : ''} ${
                    index === filteredSuggestions.length - 1 ? 'rounded-b-md' : ''
                  }`}
                >
                  {suggestion}
                </button>
              ))}
            </div>
          )}
        </div>
        <Button type='button' onClick={() => handleAddService()} variant='outline'>
          Add
        </Button>
      </div>

      {errors.services && (
        <p className='text-xs text-red-600'>{errors.services.message}</p>
      )}
    </div>
  )
}
