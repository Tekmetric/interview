import { ApolloClient, HttpLink, InMemoryCache } from '@tekmetric/graphql'

import { createErrorLink } from '../create-error-link/create-error-link'

export const makeClient = (): ApolloClient<unknown> => {
  const URI = process.env.NEXT_PUBLIC_TEKMETRIC_API_URL ?? ''

  const errorLink = createErrorLink()

  const httpLink = new HttpLink({
    uri: `${URI}/graphql`,
    fetchOptions: { cache: 'no-store', mode: 'cors' },
    credentials: 'include'
  })

  return new ApolloClient({
    cache: new InMemoryCache(),
    link: errorLink.concat(httpLink)
  })
}
