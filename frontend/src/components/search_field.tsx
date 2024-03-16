import tw, { styled } from 'twin.macro'
import { useDebouncedCallback } from 'use-debounce'

const InputField = styled.input({
  ...tw`w-full`,
  ...tw`px-4 py-2 text-gray-700 bg-white border rounded-md  focus:(outline-none ring-0)`,
})

type SearchFieldProps = {
  debounce: number
  placeholder?: string
  defaultValue?: string
  onChange: (value: string) => void
}

const SearchField = ({ debounce, placeholder, defaultValue, onChange }: SearchFieldProps): JSX.Element => {
  const debounced = useDebouncedCallback(onChange, debounce)

  return <InputField placeholder={placeholder} defaultValue={defaultValue} onChange={e => debounced(e.target.value)} />
}

export default SearchField
