import { ApolloWrapper } from '@tekmetric/components/apollo-wrapper'
import { AuthStoreProvider } from '@tekmetric/components/auth-store-provider'
import classNames from 'classnames'
import type { Metadata } from 'next'
import { Lato } from 'next/font/google'
import type { PropsWithChildren } from 'react'

import './globals.css'

const lato = Lato({
  subsets: ['latin'],
  weight: ['400', '700'],
  variable: '--font-lato'
})

export const metadata: Metadata = {
  title: 'Tekmetric',
  description: 'Tekmetric Q&A'
}

const RootLayout = ({ children }: PropsWithChildren): JSX.Element => {
  return (
    <html lang='en'>
      <body
        className={classNames(
          lato.variable,
          'tek-font-sans',
          'tek-text-slate-800'
        )}
      >
        <AuthStoreProvider>
          <ApolloWrapper>{children}</ApolloWrapper>
        </AuthStoreProvider>
      </body>
    </html>
  )
}

export default RootLayout
