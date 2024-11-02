import { gql } from '@apollo/client'

import { QuestionFragmentDoc } from '../__generated__/graphql'

export default gql`
  query getQuestions($status: QuestionStatus!) {
    questions(status: $status) {
      id
      ...Question
    }
  }

  ${QuestionFragmentDoc}
`
