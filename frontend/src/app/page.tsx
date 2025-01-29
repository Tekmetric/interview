import type { Metadata } from 'next'
import { Suspense } from 'react'
import { Launch } from './types'

import { LoadingSkeleton } from '@/app/components/ui/loading-skeleton'
import { generatePageMetadata } from '@/app/lib/metadata'
import { getNextLaunch, getLatestLaunch } from '@/app/lib/api'

import NextLaunchSection from './components/NextLaunchSection'
import LatestLaunchSection from './components/LatestLaunchSection'
import LaunchesSection from './components/LaunchesSection'
import LaunchStatsSection from './components/LaunchStatsSection'
import RocketsSection from './components/RocketsSection'

export async function generateMetadata(): Promise<Metadata> {
  const [nextLaunch, latestLaunch] = await Promise.all([
    getNextLaunch(),
    getLatestLaunch(),
  ])

  if (!nextLaunch || !latestLaunch) {
    return generatePageMetadata({
      title: 'SpaceX Launch Dashboard',
      description: 'Track SpaceX launches in real-time',
      imageUrl: '/images/spacex-logo.svg',
    })
  }

  return generatePageMetadata({
    title: 'SpaceX Launch Dashboard',
    description: `Next launch: ${(nextLaunch as Launch).name} on ${new Date((nextLaunch as Launch).date_utc).toLocaleDateString()}. Latest launch: ${(latestLaunch as Launch).name}.`,
    imageUrl:
      (latestLaunch as Launch).links?.patch?.small ||
      '/images/default-image.png',
  })
}

export default function Home(): React.ReactElement {
  return (
    <div className="space-y-8 max-w-7xl mx-auto" data-testid="home-container">
      <section
        id="next-launch"
        className="lg:col-span-8"
        data-testid="next-launch-section"
      >
        <Suspense fallback={<LoadingSkeleton className="h-40" />}>
          <NextLaunchSection data-testid="next-launch" />
        </Suspense>
      </section>

      <section id="latest-launch" data-testid="latest-launch-section">
        <Suspense fallback={<LoadingSkeleton className="h-96" />}>
          <LatestLaunchSection data-testid="latest-launch" />
        </Suspense>
      </section>

      <section
        id="launches"
        className="grid grid-cols-1 lg:grid-cols-2 gap-8"
        data-testid="launches-section"
      >
        <Suspense fallback={<LoadingSkeleton className="h-80" />}>
          <LaunchesSection data-testid="launches" />
        </Suspense>
      </section>

      <section id="launch-stats" data-testid="launch-stats-section">
        <Suspense fallback={<LoadingSkeleton className="h-80" />}>
          <LaunchStatsSection data-testid="launch-stats" />
        </Suspense>
      </section>

      <section id="rockets" data-testid="rockets-section">
        <Suspense fallback={<LoadingSkeleton className="h-80" />}>
          <RocketsSection data-testid="rockets" />
        </Suspense>
      </section>
    </div>
  )
}
