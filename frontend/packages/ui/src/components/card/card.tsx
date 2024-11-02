import type { PropsWithChildren } from 'react'

import { CardBody } from './components/card-body/card-body'
import { CardInfo } from './components/card-info/card-info'
import { CardTitle } from './components/card-title/card-title'
import { CardClassNames } from './styles'
import type { CardVariant } from './types'

export interface CardProps {
  variant?: CardVariant
  'data-testid'?: string
}

export const Card = ({
  children,
  variant,
  'data-testid': dataTestId
}: PropsWithChildren<CardProps>): JSX.Element => (
  <div className={CardClassNames({ variant })} data-testid={dataTestId}>
    {children}
  </div>
)

Card.Title = CardTitle
Card.Info = CardInfo
Card.Body = CardBody
