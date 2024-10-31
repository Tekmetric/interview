'use client'

import { gql, useMutation } from '@apollo/client'

import { LoginDocument } from '../../__generated__/graphql'

export default gql`
  mutation login($email: String!, $password: String!) {
    login(email: $email, password: $password) {
      userId
    }
  }
`
