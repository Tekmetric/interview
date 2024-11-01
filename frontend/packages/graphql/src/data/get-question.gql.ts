import { gql } from '@apollo/client'

import {
  AnswerFragmentDoc,
  QuestionWithAnswersFragmentDoc
} from '../__generated__/graphql'

export default gql`
  query getQuestion($id: String!) {
    question(id: $id) {
      id
      ...QuestionWithAnswers
    }
  }

  ${QuestionWithAnswersFragmentDoc}
  ${AnswerFragmentDoc}
`
