import { QuestionStatus } from '@tekmetric/graphql'
import { Button } from '@tekmetric/ui/button'
import { Icon } from '@tekmetric/ui/icon'

interface QuestionActionsProps {
  status: QuestionStatus
  canResolve: boolean
}

export const QuestionActions = ({
  canResolve,
  status
}: QuestionActionsProps): JSX.Element | null => {
  const canReply = status === QuestionStatus.Pending

  if (!canReply) {
    return null
  }

  return (
    <>
      {canResolve && (
        <Button variant='secondary' size='small'>
          <Icon icon='check' /> Resolve
        </Button>
      )}

      <Button size='small'>
        <Icon icon='chat' /> Answer
      </Button>
    </>
  )
}
