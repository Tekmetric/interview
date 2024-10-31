import type { PropsWithChildren, ReactNode } from 'react'

import { Typography } from '../../../typography/typography'
import { CardTitleActionsClassNames, CardTitleClassNames } from './styles'

export interface CardTitleProps {
  actions?: ReactNode
}

export const CardTitle = ({
  actions,
  children
}: PropsWithChildren<CardTitleProps>): JSX.Element => (
  <div className={CardTitleClassNames}>
    <div className='flex-1'>
      <Typography as='h2' size='md'>
        {children}
      </Typography>
    </div>

    {Boolean(actions) && (
      <div className={CardTitleActionsClassNames}>{actions}</div>
    )}
  </div>
)
