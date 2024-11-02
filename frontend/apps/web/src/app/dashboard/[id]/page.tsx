import { Page } from '@tekmetric/components/page'
import { Question } from '@tekmetric/components/question'

const QuestionPage = async ({
  params
}: {
  params: Promise<{ id: string }>
}): Promise<JSX.Element> => {
  const { id: questionId } = await params

  return (
    <Page>
      <Page.Body>
        <Question questionId={questionId} />
      </Page.Body>
    </Page>
  )
}

export default QuestionPage
