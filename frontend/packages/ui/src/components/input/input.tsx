import type { InputHTMLAttributes } from 'react'

import { InputClassNames } from './styles'

export const Input = (
  props: InputHTMLAttributes<HTMLInputElement>
): JSX.Element => <input {...props} className={InputClassNames} />
