import { Icon } from '@iconify/react'
import { useCallback, useState } from 'react'
import tw, { styled } from 'twin.macro'

const DropdownField = styled.input({
  ...tw`w-full cursor-pointer`,
  ...tw`px-4 py-2 text-gray-700 bg-white border rounded-md  focus:(outline-none ring-0)`,
})

const DropdownItemsContainer = styled.div({
  ...tw`absolute w-full bottom-0 translate-y-[103%]`,
  ...tw`rounded-md bg-white`,
  ...tw`flex flex-col gap-[8px] shadow-md shadow-gray-300`,
})

const DropdownItem = styled.div({
  ...tw`cursor-pointer`,
  ...tw`px-4 py-2  hover:bg-gray-300`,
})

type DropdownProps<T> = {
  placeholder?: string
  defaultKey?: string
  options: { [key: string]: { label: string; value: T } }
  clearable?: boolean
  onChange: (value: T | undefined) => void
}

const Dropdown = <T,>({ placeholder, defaultKey, options, clearable, onChange }: DropdownProps<T>): JSX.Element => {
  const [open, setOpen] = useState<boolean>()
  const [value, setValue] = useState<{ label: string; value: T } | undefined>(
    defaultKey ? options[defaultKey] : undefined,
  )

  const handleSelection = useCallback(
    (v: { label: string; value: T } | undefined) => {
      setValue(v)
      onChange(v?.value)
      setOpen(false)
    },
    [onChange],
  )

  return (
    <div tw="relative">
      <Icon
        icon={open ? 'vaadin:chevron-up' : 'vaadin:chevron-down'}
        tw="absolute top-0 bottom-0 right-[8px] m-auto pointer-events-none"
      />
      {clearable && value && (
        <Icon
          icon={'pajamas:clear'}
          tw="absolute top-0 bottom-0 right-[38px] m-auto cursor-pointer"
          onClick={() => handleSelection(undefined)}
        />
      )}
      <DropdownField readOnly placeholder={value?.label ?? placeholder} onClick={() => setOpen(o => !o)} />
      {open && (
        <DropdownItemsContainer>
          {Object.entries(options).map(e => (
            <DropdownItem key={e[0]} onClick={() => handleSelection(e[1])}>
              {e[1].label}
            </DropdownItem>
          ))}
        </DropdownItemsContainer>
      )}
    </div>
  )
}

export default Dropdown
