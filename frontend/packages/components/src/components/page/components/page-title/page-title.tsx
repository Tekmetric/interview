import { Typography } from '@tekmetric/ui/typography'
import type { PropsWithChildren } from 'react'

export const PageTitle = ({ children }: PropsWithChildren): JSX.Element => (
  <Typography as='h1' size='xl'>
    {children}
  </Typography>
)
