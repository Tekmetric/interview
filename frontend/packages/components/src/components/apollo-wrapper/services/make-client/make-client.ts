import { ApolloClient, HttpLink, InMemoryCache } from '@tekmetric/graphql'

export const makeClient = (): ApolloClient<unknown> => {
  const URI = process.env.NEXT_PUBLIC_TEKMETRIC_API_URL ?? ''

  const httpLink = new HttpLink({
    uri: `${URI}/graphql`,
    fetchOptions: { cache: 'no-store' }
  })

  return new ApolloClient({
    cache: new InMemoryCache(),
    link: httpLink
  })
}
