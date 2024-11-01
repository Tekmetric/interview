'use client'

import {
  GetQuestionsDocument,
  type QuestionFragment,
  type QuestionStatus,
  useQuery
} from '@tekmetric/graphql'
import { Card } from '@tekmetric/ui/card'

import { Question } from './components/question/question'

interface QuestionProps {
  status: QuestionStatus
}

export const Questions = ({ status }: QuestionProps): JSX.Element => {
  const { data, loading } = useQuery(GetQuestionsDocument, {
    variables: { status }
  })

  if (!data && !loading) {
    return (
      <Card>
        <Card.Body>There are no questions to display.</Card.Body>
      </Card>
    )
  }

  return (
    <>
      {data?.questions.map((question) => (
        <Question key={question.id} question={question as QuestionFragment} />
      ))}
    </>
  )
}
