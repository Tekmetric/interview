import type { PropsWithChildren } from 'react'

import { Typography } from '../../../typography/typography'

export const CardBody = ({ children }: PropsWithChildren): JSX.Element => (
  <Typography as='div' size='sm'>
    {children}
  </Typography>
)
