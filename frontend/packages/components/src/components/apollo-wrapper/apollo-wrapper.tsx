'use client'

import { ApolloNextAppProvider } from '@tekmetric/graphql'
import type { PropsWithChildren } from 'react'

import { makeClient } from './services/make-client/make-client'

export const ApolloWrapper = ({ children }: PropsWithChildren): JSX.Element => (
  <ApolloNextAppProvider makeClient={makeClient}>
    {children}
  </ApolloNextAppProvider>
)
