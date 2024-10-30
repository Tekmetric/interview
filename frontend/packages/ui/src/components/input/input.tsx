import type { InputHTMLAttributes } from 'react'

import { InputClassNames } from './styles'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  width?: 'default' | 'full'
}

export const Input = ({ width, ...rest }: InputProps): JSX.Element => (
  <input {...rest} className={InputClassNames({ width })} />
)
