import { Page } from '@tekmetric/components/page'
import { Questions } from '@tekmetric/components/questions'
import { QuestionStatus } from '@tekmetric/graphql'

const DashboardPage = (): JSX.Element => (
  <Page>
    <Page.Title>Pending Questions</Page.Title>

    <Page.Body>
      <Questions status={QuestionStatus.Pending} />
    </Page.Body>
  </Page>
)

export default DashboardPage
