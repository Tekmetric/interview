import { type QuestionWithAnswersFragment } from '@tekmetric/graphql'
import { Card } from '@tekmetric/ui/card'

import { formatCreatedDate } from '../../../../services/format-created-date/format-created-date'
import { AnswersClassNames } from './styles'

interface AnswersProps {
  answers: QuestionWithAnswersFragment['answers']
}

export const Answers = ({ answers }: AnswersProps): JSX.Element => (
  <div className={AnswersClassNames}>
    {answers.map(
      ({ id, description, createdAt, author: { firstName, lastName } }) => {
        const created = formatCreatedDate(createdAt as string)

        return (
          <Card key={id} variant='secondary'>
            <Card.Info>
              {firstName} {lastName} ({created})
            </Card.Info>
            <Card.Body>{description}</Card.Body>
          </Card>
        )
      }
    )}
  </div>
)
