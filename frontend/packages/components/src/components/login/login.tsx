'use client'

import { LoginDocument, useMutation } from '@tekmetric/graphql'
import { Button } from '@tekmetric/ui/button'
import { Field } from '@tekmetric/ui/field'
import { Form } from '@tekmetric/ui/form'
import { Input } from '@tekmetric/ui/input'
import { validateSchema } from '@tekmetric/ui/validate-schema'
import { useCallback } from 'react'

import { loginValidationSchema } from './constants'
import { LoginClassNames } from './styles'
import type { LoginFormFields } from './types'

export const Login = (): JSX.Element => {
  const [login] = useMutation(LoginDocument)

  const handleSubmit = useCallback(
    async ({ email, password }: LoginFormFields) => {
      await login({ variables: { email, password } })
    },
    [login]
  )

  return (
    <Form<LoginFormFields>
      onSubmit={handleSubmit}
      validate={validateSchema(loginValidationSchema)}
    >
      <div className={LoginClassNames}>
        <Field<string> name='email' label='Email Address'>
          {({ input }) => (
            <Input
              {...input}
              id={input.name}
              required
              type='email'
              name={input.name}
              width='full'
              autoComplete='email'
            />
          )}
        </Field>

        <Field<string> name='password' label='Password'>
          {({ input }) => (
            <Input
              {...input}
              id={input.name}
              required
              type='password'
              name={input.name}
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
    </Form>
  )
}
