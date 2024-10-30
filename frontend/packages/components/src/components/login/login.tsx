import { Button } from '@tekmetric/ui/button'
import { Field } from '@tekmetric/ui/field'
import { Input } from '@tekmetric/ui/input'

import { LoginClassNames } from './styles'

export const Login = (): JSX.Element => (
  <div className={LoginClassNames}>
    <Field name='email' label='Email Address'>
      {({ name }: { name: string }) => (
        <Input
          id={name}
          required
          type='email'
          name={name}
          width='full'
          autoComplete='email'
        />
      )}
    </Field>

    <Field name='password' label='Password'>
      {({ name }: { name: string }) => (
        <Input
          id={name}
          required
          type='password'
          name={name}
          width='full'
          autoComplete='password'
        />
      )}
    </Field>

    <div className='tek-pt-4'>
      <Button type='submit' width='full'>
        Sign in
      </Button>
    </div>
  </div>
)
