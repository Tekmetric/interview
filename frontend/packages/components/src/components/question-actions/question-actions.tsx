import { QuestionStatus } from '@tekmetric/graphql'
import { Icon } from '@tekmetric/ui/icon'

import { ResolveButton } from './components/resolve-button/resolve-button'
import { ViewQuestionButton } from './components/view-question-button/view-question-button'

interface QuestionActionsProps {
  questionId: string
  status: QuestionStatus
  canResolve: boolean
}

export const QuestionActions = ({
  questionId,
  canResolve,
  status
}: QuestionActionsProps): JSX.Element | null => {
  const isPending = status === QuestionStatus.Pending

  return (
    <>
      {isPending && canResolve && <ResolveButton questionId={questionId} />}

      {!isPending && (
        <ViewQuestionButton questionId={questionId} variant='secondary'>
          View
        </ViewQuestionButton>
      )}

      {isPending && (
        <ViewQuestionButton questionId={questionId}>
          <Icon icon='chat' /> Answer
        </ViewQuestionButton>
      )}
    </>
  )
}
