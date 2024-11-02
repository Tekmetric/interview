import type { PropsWithChildren } from 'react'

import { Typography } from '../../../typography/typography'
import { CardInfoClassNames } from './styles'

export const CardInfo = ({ children }: PropsWithChildren): JSX.Element => (
  <div className={CardInfoClassNames}>
    <Typography as='p' size='sm' color='slate-500'>
      {children}
    </Typography>
  </div>
)
