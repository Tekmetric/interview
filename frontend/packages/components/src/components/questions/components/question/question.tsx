import { type QuestionFragment } from '@tekmetric/graphql'
import { Card } from '@tekmetric/ui/card'

import { formatCreatedDate } from '../../../../services/format-created-date/format-created-date'
import { QuestionActions } from '../../../question-actions/question-actions'

interface QuestionProps {
  question: QuestionFragment
}

export const Question = ({
  question: {
    id: questionId,
    title,
    shortDescription,
    status,
    createdAt,
    author: { firstName, lastName },
    permissions: { canResolve }
  }
}: QuestionProps): JSX.Element => {
  const created = formatCreatedDate(createdAt as string)

  return (
    <Card>
      <Card.Title
        actions={
          <QuestionActions
            questionId={questionId}
            canResolve={canResolve}
            status={status}
          />
        }
      >
        {title}
      </Card.Title>
      <Card.Info>
        {firstName} {lastName} ({created})
      </Card.Info>
      <Card.Body>{shortDescription}...</Card.Body>
    </Card>
  )
}
