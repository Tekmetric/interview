import type { InputHTMLAttributes } from 'react'

import { InputClassNames } from './styles'
import type { InputWidth } from './types'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  width?: InputWidth
}

export const Input = ({ width, ...rest }: InputProps): JSX.Element => (
  <input {...rest} className={InputClassNames({ width })} />
)
