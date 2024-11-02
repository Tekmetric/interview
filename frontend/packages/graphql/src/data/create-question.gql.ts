import { gql } from '@apollo/client'

export default gql`
  mutation createQuestion($input: CreateQuestionDto!) {
    createQuestion(input: $input) {
      id
      title
      description
    }
  }
`
