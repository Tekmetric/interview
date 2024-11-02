import { gql } from '@apollo/client'

export default gql`
  mutation createAnswer($input: CreateAnswerDto!) {
    createAnswer(input: $input) {
      id
      description
    }
  }
`
