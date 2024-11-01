import { ApolloWrapper } from '@tekmetric/components/apollo-wrapper'
import { AuthStoreProvider } from '@tekmetric/components/auth-store-provider'
import { ToastContainer } from '@tekmetric/components/toast-container'
import classNames from 'classnames'
import type { Metadata } from 'next'
import { Lato } from 'next/font/google'
import { cookies } from 'next/headers'
import type { PropsWithChildren } from 'react'

import { SESSION_KEY } from './constants'
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

const RootLayout = async ({
  children
}: PropsWithChildren): Promise<JSX.Element> => {
  const cookieStore = await cookies()
  const session = cookieStore.get(SESSION_KEY)?.value ?? null

  return (
    <html lang='en'>
      <body
        className={classNames(
          lato.variable,
          'tek-font-sans',
          'tek-text-slate-800'
        )}
      >
        <AuthStoreProvider session={session}>
          <ApolloWrapper>
            {children}

            <ToastContainer />
          </ApolloWrapper>
        </AuthStoreProvider>
      </body>
    </html>
  )
}

export default RootLayout
