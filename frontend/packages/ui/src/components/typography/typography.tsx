import type { PropsWithChildren } from 'react'

import { TypographyClassNames } from './styles'
import type {
  TypographyAlignment,
  TypographyAs,
  TypographySize,
  TypographyWeight
} from './types'

interface TypographyProps {
  as?: TypographyAs
  alignment?: TypographyAlignment
  size?: TypographySize
  weight?: TypographyWeight
}

export const Typography = ({
  as: Element = 'p',
  alignment = 'left',
  size = 'md',
  weight = 'medium',
  children
}: PropsWithChildren<TypographyProps>): JSX.Element => {
  return (
    <Element className={TypographyClassNames({ alignment, size, weight })}>
      {children}
    </Element>
  )
}
