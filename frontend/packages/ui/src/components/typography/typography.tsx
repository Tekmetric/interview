import type { PropsWithChildren } from 'react'

import { TypographyClassNames } from './styles'
import type {
  TypographyAlignment,
  TypographyAs,
  TypographyColor,
  TypographySize,
  TypographyWeight
} from './types'

interface TypographyProps {
  as?: TypographyAs
  alignment?: TypographyAlignment
  size?: TypographySize
  weight?: TypographyWeight
  color?: TypographyColor
}

export const Typography = ({
  as: Element = 'p',
  alignment = 'left',
  size = 'md',
  weight = 'medium',
  color = 'current',
  children
}: PropsWithChildren<TypographyProps>): JSX.Element => {
  return (
    <Element
      className={TypographyClassNames({ alignment, size, weight, color })}
    >
      {children}
    </Element>
  )
}
