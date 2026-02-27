import createFetchClient from 'openapi-fetch'
import createClient from 'openapi-react-query'
import type { paths } from '@/lib/api/contracts/generated/api.types'

const fetchClient = createFetchClient<paths>({
  baseUrl: '/',
})

export const $api = createClient(fetchClient)
