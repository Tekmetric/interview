import '@tekmetric/ui/styles.css'
import classNames from 'classnames'
import type { Metadata } from 'next'
import { Inter } from 'next/font/google'

import './globals.css'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Tekmetric',
  description: 'Tekmetric Q&A'
}

const RootLayout = ({
  children
}: {
  children: React.ReactNode
}): JSX.Element => {
  return (
    <html lang='en'>
      <body className={classNames(inter.className)}>{children}</body>
    </html>
  )
}

export default RootLayout
