'use client'

import { Field } from '@tekmetric/ui/field'
import { Form } from '@tekmetric/ui/form'
import { Input } from '@tekmetric/ui/input'
import { SubmitButton } from '@tekmetric/ui/submit-button'
import { validateSchema } from '@tekmetric/ui/validate-schema'
import { ValidationError } from '@tekmetric/ui/validation-error'

import { loginValidationSchema } from './constants'
import { useLogin } from './services/use-login/use-login'
import type { LoginFormFields } from './types'

export const Login = (): JSX.Element => {
  const { handleSubmit, hasGlobalError } = useLogin()

  return (
    <Form<LoginFormFields>
      onSubmit={handleSubmit}
      validate={validateSchema(loginValidationSchema)}
    >
      <Field<string> name='email' label='Email Address'>
        {({ input, options: { disabled } }) => (
          <Input
            {...input}
            id={input.name}
            required
            disabled={disabled}
            type='email'
            name={input.name}
            width='full'
            autoComplete='email'
          />
        )}
      </Field>

      <Field<string> name='password' label='Password'>
        {({ input, options: { disabled } }) => (
          <Input
            {...input}
            id={input.name}
            required
            disabled={disabled}
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
        <SubmitButton type='submit' width='full'>
          Sign in
        </SubmitButton>
      </div>
    </Form>
  )
}