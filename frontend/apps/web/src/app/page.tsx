import { Login } from '@tekmetric/components/login'
import { Typography } from '@tekmetric/ui/typography'

import { PageClassNames } from './styles'

const Page = (): JSX.Element => (
  <main className={PageClassNames}>
    <Typography as='h1' size='2xl' weight='semibold' alignment='center'>
      Welcome to Tekmetric
    </Typography>

    <Login />
  </main>
)

export default Page
