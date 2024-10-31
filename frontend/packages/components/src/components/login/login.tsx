'use client'

import { LoginDocument, useMutation } from '@tekmetric/graphql'
import { Button } from '@tekmetric/ui/button'
import { Field } from '@tekmetric/ui/field'
import { Form } from '@tekmetric/ui/form'
import { Input } from '@tekmetric/ui/input'
import { validateSchema } from '@tekmetric/ui/validate-schema'
import { ValidationError } from '@tekmetric/ui/validation-error'
import { useRouter } from 'next/navigation'
import { useCallback } from 'react'

import { useAuthStore } from '../../services/use-auth-store/use-auth-store'
import { loginValidationSchema } from './constants'
import { LoginClassNames } from './styles'
import type { LoginFormFields } from './types'

export const Login = (): JSX.Element => {
  const router = useRouter()
  const [login, { error: globalError }] = useMutation(LoginDocument)
  const { setSession } = useAuthStore((state) => state)
  const hasGlobalError = Boolean(globalError)

  const handleSubmit = useCallback(
    async ({ email, password }: LoginFormFields) => {
      const result = await login({ variables: { email, password } })

      if (result.data?.login?.userId && !result.errors?.length) {
        setSession({ userId: result.data.login.userId })

        router.push(`/dashboard`)
      }
    },
    [login, router, setSession]
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

        {hasGlobalError && (
          <ValidationError>
            Credentials are invalid. Please try again.
          </ValidationError>
        )}

        <div className='tek-pt-4'>
          <Button type='submit' width='full'>
            Sign in
          </Button>
        </div>
      </div>
    </Form>
  )
}
