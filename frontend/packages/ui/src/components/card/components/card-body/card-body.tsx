import type { PropsWithChildren } from 'react'

import { Typography } from '../../../typography/typography'

export const CardBody = ({ children }: PropsWithChildren): JSX.Element => (
  <Typography as='p' size='sm'>
    {children}
  </Typography>
)