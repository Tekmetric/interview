'use client'

import { Card } from '@tekmetric/ui/card'
import { Field } from '@tekmetric/ui/field'
import { Form } from '@tekmetric/ui/form'
import { SubmitButton } from '@tekmetric/ui/submit-button'
import { TextBox } from '@tekmetric/ui/text-box'
import { validateSchema } from '@tekmetric/ui/validate-schema'

import { createAnswerValidationSchema } from './constants'
import { useCreateAnswer } from './services/use-create-answer/use-create-answer'
import type { CreateAnswerFormFields } from './types'

interface CreateAnswerProps {
  questionId: string
}

export const CreateAnswer = ({
  questionId
}: CreateAnswerProps): JSX.Element => {
  const { createAnswer } = useCreateAnswer(questionId)

  return (
    <Card>
      <Card.Title>
        <span className='tek-mb-4 tek-block'>
          Add your answer to this question.
        </span>
      </Card.Title>
      <Card.Body>
        <Form<CreateAnswerFormFields>
          onSubmit={createAnswer}
          validate={validateSchema(createAnswerValidationSchema)}
        >
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
                placeholder='Write your answer here'
              />
            )}
          </Field>

          <div className='tek-flex tek-justify-end'>
            <SubmitButton type='submit'>Create a new question</SubmitButton>
          </div>
        </Form>
      </Card.Body>
    </Card>
  )
}
