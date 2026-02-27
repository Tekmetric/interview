import Link from 'next/link'
import { LayoutDashboard } from 'lucide-react'
import { BrandMark } from '@/components/BrandMark/BrandMark'
import { StatusBadge } from '@/components/StatusBadge/StatusBadge'
import { cn } from '@/lib/classnames/cn'
import { features, mockKpis, mockOrders, navItems } from '@/lib/mocks/home-page/home-page-mocks'

export default function Home() {
  return (
    <div className="min-h-screen">
      <header className="bg-slate-950">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4 sm:px-10">
          <BrandMark />
          <nav className="hidden items-center gap-6 text-sm font-medium text-slate-400 sm:flex">
            {navItems.map((item) => (
              <Link key={item.href} href={item.href} className="transition-colors hover:text-white">
                {item.label}
              </Link>
            ))}
          </nav>
          <Link href="/dashboard" className="tm-btn-primary text-sm">
            Open App
          </Link>
        </div>
      </header>

      <section className="bg-slate-950 pb-20 pt-16">
        <div className="mx-auto max-w-7xl px-6 sm:px-10">
          <div className="grid gap-12 lg:grid-cols-[1fr_1.1fr] lg:items-center lg:gap-16">
            <div className="space-y-7">
              <p className="inline-flex items-center gap-2 rounded-full border border-brand-800 bg-brand-900/40 px-4 py-1 text-sm font-semibold text-brand-300">
                <span className="h-1.5 w-1.5 rounded-full bg-brand" />
                Built for modern auto shops
              </p>
              <h1 className="max-w-xl text-4xl font-bold leading-tight text-white sm:text-5xl lg:text-[3.25rem]">
                Shop management that moves as fast as your{' '}
                <span className="text-brand">service bay.</span>
              </h1>
              <p className="max-w-lg text-lg leading-8 text-slate-400">
                Track every repair order, customer, and vehicle in one place. Real-time KPIs so you
                always know exactly how today is going.
              </p>
              <div className="flex flex-wrap gap-3">
                <Link href="/dashboard" className="tm-btn-primary">
                  Open Dashboard
                </Link>
                <Link
                  href="/repair-orders"
                  className="inline-flex items-center justify-center rounded-full border border-slate-700 bg-transparent px-6 py-3 font-semibold text-slate-300 transition-colors hover:border-slate-500 hover:text-white"
                >
                  View Repair Orders
                </Link>
              </div>
            </div>

            <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5 shadow-2xl">
              <div className="mb-4 grid grid-cols-2 gap-3">
                {mockKpis.map((kpi) => (
                  <div
                    key={kpi.label}
                    className="rounded-xl border border-slate-800 bg-slate-800/50 p-4"
                  >
                    <div className="mb-3 flex items-center justify-between">
                      <p className="text-xs font-medium text-slate-400">{kpi.label}</p>
                      <div
                        className={cn(
                          'flex h-7 w-7 items-center justify-center rounded-lg',
                          kpi.iconClass,
                        )}
                      >
                        {kpi.icon}
                      </div>
                    </div>
                    <p className={cn('text-2xl font-bold tracking-tight', kpi.valueClass)}>
                      {kpi.value}
                    </p>
                  </div>
                ))}
              </div>

              <div className="overflow-hidden rounded-xl border border-slate-800">
                <div className="border-b border-slate-800 bg-slate-800/40 px-4 py-2.5">
                  <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Recent Repair Orders
                  </p>
                </div>
                <div className="divide-y divide-slate-800">
                  {mockOrders.map((ro) => (
                    <div key={ro.id} className="flex items-center justify-between px-4 py-3">
                      <div className="flex items-center gap-3">
                        <span className="font-mono text-xs font-semibold text-slate-500">
                          #{ro.id}
                        </span>
                        <span className="text-sm text-slate-300">{ro.vehicle}</span>
                      </div>
                      <div className="flex items-center gap-3">
                        <StatusBadge status={ro.status} />
                        <span className="text-sm font-semibold text-white">{ro.total}</span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="py-20">
        <div className="mx-auto max-w-7xl px-6 sm:px-10">
          <div className="mb-12 max-w-2xl">
            <h2 className="mb-3 text-3xl font-bold sm:text-4xl">
              Everything your shop needs, nothing it doesn&apos;t.
            </h2>
            <p className="text-lg text-muted">
              Three modules built around how shops actually work - not how software vendors think
              they work.
            </p>
          </div>

          <div className="grid gap-6 sm:grid-cols-3">
            {features.map((feature) => (
              <Link key={feature.title} href={feature.href} className="group">
                <div className="tm-card flex h-full flex-col gap-5 p-7 transition-shadow hover:shadow-[0_16px_40px_-20px_rgba(22,31,48,0.35)]">
                  <div
                    className={cn(
                      'inline-flex h-11 w-11 items-center justify-center rounded-xl border',
                      feature.colorClass,
                    )}
                  >
                    {feature.icon}
                  </div>
                  <div className="flex-1 space-y-2">
                    <h3 className="text-base font-semibold">{feature.title}</h3>
                    <p className="text-sm leading-6 text-muted">{feature.description}</p>
                  </div>
                  <div className="flex items-center justify-between border-t border-border pt-4">
                    <span className="text-xs font-semibold text-slate-500">{feature.stat}</span>
                    <span className="text-xs font-semibold text-brand opacity-0 transition-opacity group-hover:opacity-100">
                      Open <LayoutDashboard className="inline h-3.5 w-3.5" />
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      </section>

      <section className="bg-slate-950 py-20">
        <div className="mx-auto max-w-2xl px-6 text-center sm:px-10">
          <h2 className="mb-4 text-3xl font-bold text-white sm:text-4xl">
            Ready to see it in action?
          </h2>
          <p className="mb-8 text-lg text-slate-400">
            Jump straight into the live dashboard - real data, no setup required.
          </p>
          <Link href="/dashboard" className="tm-btn-primary text-base">
            Open Dashboard
          </Link>
        </div>
      </section>
    </div>
  )
}
