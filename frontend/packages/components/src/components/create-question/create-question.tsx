'use client'

import { Field } from '@tekmetric/ui/field'
import { Form } from '@tekmetric/ui/form'
import { Input } from '@tekmetric/ui/input'
import { SubmitButton } from '@tekmetric/ui/submit-button'
import { TextBox } from '@tekmetric/ui/text-box'
import { validateSchema } from '@tekmetric/ui/validate-schema'

import { createQuestionValidationSchema } from './constants'
import { useCreateQuestion } from './services/use-create-question/use-create-question'
import type { CreateQuestionFormFields } from './types'

export const CreateQuestion = (): JSX.Element => {
  const { createQuestion } = useCreateQuestion()

  return (
    <Form<CreateQuestionFormFields>
      onSubmit={createQuestion}
      validate={validateSchema(createQuestionValidationSchema)}
    >
      <Field<string> name='title' label='Title'>
        {({ input, options: { disabled } }) => (
          <Input
            {...input}
            id={input.name}
            required
            disabled={disabled}
            type='text'
            name={input.name}
            width='full'
            autoComplete='question-title'
            placeholder='What you want to ask?'
            data-testid='create-question-title'
          />
        )}
      </Field>

      <Field<string> name='description' label='Description'>
        {({ input, options: { disabled } }) => (
          <TextBox
            {...input}
            id={input.name}
            required
            disabled={disabled}
            type='text'
            name={input.name}
            width='full'
            autoComplete='question-description'
            placeholder='Write a description for your question'
            data-testid='create-question-description'
          />
        )}
      </Field>

      <div className='tek-flex tek-justify-end'>
        <SubmitButton type='submit' data-testid='submit-button'>
          Create a new question
        </SubmitButton>
      </div>
    </Form>
  )
}
