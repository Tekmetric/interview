import { type ApolloLink, onError } from '@tekmetric/graphql'

import { AuthStorage } from '../../../../services/auth-storage/auth-storage'

export const createErrorLink = (): ApolloLink =>
  onError(({ graphQLErrors }) => {
    if (graphQLErrors) {
      for (const err of graphQLErrors) {
        switch (err.extensions?.code) {
          case 'UNAUTHENTICATED': {
            AuthStorage.removeSession()

            window.location.reload()
          }
        }
      }
    }
  })
