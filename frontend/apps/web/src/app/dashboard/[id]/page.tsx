import { Page } from '@tekmetric/components/page'
import { Questions } from '@tekmetric/components/questions'
import { QuestionStatus } from '@tekmetric/graphql'

const QuestionPage = (): JSX.Element => {
  return (
    <Page>
      <Page.Title>Completed Questions</Page.Title>

      <Page.Body>
        <Questions status={QuestionStatus.Completed} />
      </Page.Body>
    </Page>
  )
}

export default QuestionPage
