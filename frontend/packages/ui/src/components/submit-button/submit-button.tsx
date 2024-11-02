import type { PropsWithChildren } from 'react'
import { useFormState } from 'react-final-form'

import { Button, type ButtonProps } from '../button/button'

export const SubmitButton = ({
  children,
  ...rest
}: PropsWithChildren<ButtonProps>): JSX.Element => {
  const { submitting } = useFormState({ subscription: { submitting: true } })
  const disabled = rest.disabled ?? submitting

  return (
    <Button {...rest} disabled={disabled}>
      {children}
    </Button>
  )
}
