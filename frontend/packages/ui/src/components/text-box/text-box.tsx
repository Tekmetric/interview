import type { InputHTMLAttributes } from 'react'

import { TextBoxClassNames } from './styles'
import type { TextBoxWidth } from './types'

interface TextBoxProps extends InputHTMLAttributes<HTMLTextAreaElement> {
  width?: TextBoxWidth
}

export const TextBox = ({ width, ...rest }: TextBoxProps): JSX.Element => (
  <textarea {...rest} className={TextBoxClassNames({ width })} />
)
