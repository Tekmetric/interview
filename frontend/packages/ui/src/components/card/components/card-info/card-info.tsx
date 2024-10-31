import type { PropsWithChildren } from 'react'

import { Typography } from '../../../typography/typography'

export const CardInfo = ({ children }: PropsWithChildren): JSX.Element => (
  <Typography as='p' size='sm' color='slate-500'>
    {children}
  </Typography>
)
