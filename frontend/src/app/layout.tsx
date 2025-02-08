import './globals.css'
import { Inter } from 'next/font/google'
import type { Metadata, Viewport } from 'next'
import type { ReactNode } from 'react'

import { Analytics } from '@vercel/analytics/react'
import { SpeedInsights } from '@vercel/speed-insights/next'

import Header from '@/app/components/Header/Header'
import SmoothScroll from '@/app/components/SmoothScroll'
import RegisterServiceWorker from '@/app/components/RegisterServiceWorker'
import Script from 'next/script'

const inter = Inter({
  subsets: ['latin'],
  display: 'swap',
  preload: true,
})

export const metadata: Metadata = {
  metadataBase: new URL('https://tekmetric-interview.vercel.app'),
  title: 'SpaceX Launch Dashboard',
  description: 'Track SpaceX launches in real-time',
  openGraph: {
    title: 'SpaceX Launch Dashboard',
    description: 'Track SpaceX launches in real-time',
    type: 'website',
    url: 'https://tekmetric-interview.vercel.app/',
  },
  twitter: {
    card: 'summary_large_image',
    title: 'SpaceX Launch Dashboard',
    description: 'Track SpaceX launches in real-time',
  },
}

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
}

interface RootLayoutProps {
  children: ReactNode
}

export default function RootLayout({
  children,
}: RootLayoutProps): React.ReactElement {
  return (
    <html lang="en" className={inter.className} data-testid="root-layout">
      <head>
        <link rel="preconnect" href="https://api.spacexdata.com" />
        <link rel="preconnect" href="https://images2.imgbox.com" />
        <link
          rel="preconnect"
          href="https://fonts.gstatic.com"
          crossOrigin="anonymous"
        />
        <link
          rel="stylesheet"
          href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
          integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY="
          crossOrigin=""
        />
        <Script
          src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"
          integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo="
          strategy="beforeInteractive"
          crossOrigin=""
        />
        <link rel="preconnect" href="https://a.tile.openstreetmap.org" />
        <link rel="preconnect" href="https://b.tile.openstreetmap.org" />
        <link rel="preconnect" href="https://c.tile.openstreetmap.org" />
      </head>
      <body className="min-h-screen bg-gradient-to-br from-gray-900 via-slate-900 to-gray-950 text-foreground flex flex-col">
        <SmoothScroll />
        <a
          href="#main-content"
          className="sr-only focus:not-sr-only bg-primary text-primary-foreground p-3 m-3 transition duration-150 ease-in-out absolute top-0 left-0 -translate-y-full focus:translate-y-0 z-50"
        >
          Skip to main content
        </a>
        <Header data-testid="header" />
        <main
          id="main-content"
          className="flex-grow"
          data-testid="main-content"
        >
          <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
            {children}
          </div>
        </main>
        <footer
          className="bg-card/50 backdrop-blur-sm text-card-foreground py-8"
          data-testid="footer"
        >
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col items-center">
            <p
              className="text-center mb-4 hover:underline"
              data-testid="footer-text"
            >
              &copy; {new Date().getFullYear()} SpaceX Launch Dashboard. All
              rights reserved.
            </p>
          </div>
        </footer>
        <RegisterServiceWorker />
        <SpeedInsights />
        <Analytics />
      </body>
    </html>
  )
}
