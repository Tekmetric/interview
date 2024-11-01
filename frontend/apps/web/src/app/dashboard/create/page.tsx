import { CreateQuestion } from '@tekmetric/components/create-question'
import { Page } from '@tekmetric/components/page'

const CreatePage = (): JSX.Element => (
  <Page>
    <Page.Title>Create Questions</Page.Title>

    <Page.Body>
      <CreateQuestion />
    </Page.Body>
  </Page>
)

export default CreatePage
