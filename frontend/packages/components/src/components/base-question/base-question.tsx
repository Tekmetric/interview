import type {
  QuestionFragment,
  QuestionWithAnswersFragment
} from '@tekmetric/graphql'
import { Card } from '@tekmetric/ui/card'

import { formatCreatedDate } from '../../services/format-created-date/format-created-date'
import { QuestionActions } from '../question-actions/question-actions'
import { getDescriptions } from './services/get-description/get-description'

interface BaseQuestionProps {
  question: QuestionFragment | QuestionWithAnswersFragment
  hideViewButton?: boolean
}

export const BaseQuestion = ({
  question,
  hideViewButton
}: BaseQuestionProps): JSX.Element => {
  const {
    id: questionId,
    title,
    status,
    author: { firstName, lastName },
    permissions: { canResolve }
  } = question

  const created = formatCreatedDate(question.createdAt as string)
  const description = getDescriptions(question)

  return (
    <Card>
      <Card.Title
        actions={
          <QuestionActions
            questionId={questionId}
            canResolve={Boolean(canResolve)}
            status={status}
            hideViewButton={hideViewButton}
          />
        }
      >
        {title}
      </Card.Title>
      <Card.Info>
        {firstName} {lastName} ({created})
      </Card.Info>
      <Card.Body>{description}</Card.Body>
    </Card>
  )
}
