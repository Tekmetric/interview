import { Input } from '@tekmetric/ui/input'

import { LoginClassNames } from './styles'

export const Login = (): JSX.Element => (
  <div className={LoginClassNames}>
    <Input placeholder='Email Address' name='email' />
  </div>
)
