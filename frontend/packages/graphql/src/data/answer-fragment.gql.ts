import { gql } from '@apollo/client'

export default gql`
  fragment Answer on Answer {
    id
    description
    createdAt
    author {
      id
      firstName
      lastName
    }
  }
`
