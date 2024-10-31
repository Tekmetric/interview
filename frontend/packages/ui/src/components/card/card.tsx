import type { PropsWithChildren } from 'react'

import { CardBody } from './components/card-body/card-body'
import { CardInfo } from './components/card-info/card-info'
import { CardTitle } from './components/card-title/card-title'
import { CardClassNames } from './styles'

export const Card = ({ children }: PropsWithChildren): JSX.Element => (
  <div className={CardClassNames}>{children}</div>
)

Card.Title = CardTitle
Card.Info = CardInfo
Card.Body = CardBody
