import { gql } from '@apollo/client'

import {
  AnswerFragmentDoc,
  QuestionFragmentDoc
} from '../__generated__/graphql'

export default gql`
  fragment QuestionWithAnswers on Question {
    id
    ...Question
    description
    answers {
      id
      ...Answer
    }
  }

  ${QuestionFragmentDoc}
  ${AnswerFragmentDoc}
`
