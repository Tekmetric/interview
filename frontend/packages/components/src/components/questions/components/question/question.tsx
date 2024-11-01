import { type QuestionFragment } from '@tekmetric/graphql'
import { Card } from '@tekmetric/ui/card'

import { QuestionActions } from '../question-actions/question-actions'

interface QuestionProps {
  question: QuestionFragment
}

export const Question = ({
  question: {
    title,
    shortDescription,
    status,
    author: { firstName, lastName },
    permissions: { canResolve }
  }
}: QuestionProps): JSX.Element => (
  <Card>
    <Card.Title
      actions={<QuestionActions canResolve={canResolve} status={status} />}
    >
      {title}
    </Card.Title>
    <Card.Info>
      {firstName} {lastName} (2 days ago)
    </Card.Info>
    <Card.Body>{shortDescription}</Card.Body>
  </Card>
)
