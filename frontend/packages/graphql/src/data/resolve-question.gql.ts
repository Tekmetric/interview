import { gql } from '@apollo/client'

export default gql`
  mutation resolveQuestion($id: String!) {
    resolveQuestion(id: $id) {
      id
      status
    }
  }
`
