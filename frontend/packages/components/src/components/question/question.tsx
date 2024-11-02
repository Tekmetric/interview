'use client'

import {
  GetQuestionDocument,
  QuestionStatus,
  useQuery
} from '@tekmetric/graphql'
import { Card } from '@tekmetric/ui/card'

import { BaseQuestion } from '../base-question/base-question'
import { CardsSkeletonLoader } from '../cards-skeleton-loader/cards-skeleton-loader'
import { Answers } from './components/answers/answers'
import { CreateAnswer } from './components/create-answer/create-answer'
import { QuestionClassNames } from './styles'

interface QuestionProps {
  questionId: string
}

export const Question = ({ questionId }: QuestionProps): JSX.Element | null => {
  const { data, loading } = useQuery(GetQuestionDocument, {
    variables: { id: questionId }
  })

  if (loading && !data) {
    return <CardsSkeletonLoader cards={1} />
  }

  if (!data) {
    return (
      <Card>
        <Card.Body>There is no question to display.</Card.Body>
      </Card>
    )
  }

  return (
    <div className={QuestionClassNames}>
      <BaseQuestion hideViewButton question={data.question} />
      <Answers answers={data.question.answers} />

      {data.question.status === QuestionStatus.Pending && (
        <CreateAnswer questionId={questionId} />
      )}
    </div>
  )
}
