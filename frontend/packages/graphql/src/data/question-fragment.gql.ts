import { gql } from '@apollo/client'

export default gql`
  fragment Question on Question {
    id
    title
    shortDescription
    status
    createdAt
    author {
      id
      firstName
      lastName
    }
    permissions {
      id
      canResolve
    }
  }
`
