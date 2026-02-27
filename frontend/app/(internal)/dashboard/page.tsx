'use client'

import { $api } from '@/lib/api/client/api-client'
import { KpiCard } from '@/components/KpiCard/KpiCard'
import { PageTitle } from '@/components/PageTitle/PageTitle'
import { BarChart3, Car, CircleDollarSign, Wrench } from 'lucide-react'
import { formatUSD } from '@/lib/format/currency'

function SkeletonCard() {
  return (
    <div className="tm-card animate-pulse p-6">
      <div className="flex items-center justify-between">
        <div className="h-4 w-28 rounded bg-slate-200" />
        <div className="h-10 w-10 rounded-xl bg-slate-200" />
      </div>
      <div className="mt-4 h-9 w-36 rounded bg-slate-200" />
    </div>
  )
}

export default function DashboardPage() {
  const { data, isLoading, isError } = $api.useQuery('get', '/api/v1/dashboard/kpis')

  if (isLoading) {
    return (
      <div>
        <PageTitle title="Dashboard" />
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, i) => (
            <SkeletonCard key={i} />
          ))}
        </div>
      </div>
    )
  }

  if (isError || !data) {
    return (
      <div>
        <PageTitle title="Dashboard" />
        <p className="text-sm text-red-600">Failed to load KPI data.</p>
      </div>
    )
  }

  return (
    <div>
      <PageTitle title="Dashboard" />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <KpiCard
          title="Cars Today"
          value={String(data.carCountToday)}
          iconClass="bg-sky-100 text-sky-600"
          icon={<Car className="h-5 w-5" />}
        />
        <KpiCard
          title="Revenue Today"
          value={formatUSD(data.revenueToday)}
          iconClass="bg-emerald-100 text-emerald-600"
          valueClass="text-emerald-700"
          icon={<CircleDollarSign className="h-5 w-5" />}
        />
        <KpiCard
          title="Average RO"
          value={formatUSD(data.averageRO)}
          iconClass="bg-amber-100 text-amber-600"
          icon={<BarChart3 className="h-5 w-5" />}
        />
        <KpiCard
          title="Open ROs"
          value={String(data.openROs)}
          iconClass="bg-brand text-white"
          valueClass="text-brand"
          icon={<Wrench className="h-5 w-5" />}
        />
      </div>
    </div>
  )
}
