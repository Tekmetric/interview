import { Sidebar } from '@/components/Sidebar/Sidebar'

export default function InternalLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-screen overflow-hidden bg-background">
      <Sidebar />
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Top accent bar */}
        <div className="h-1 w-full shrink-0 bg-linear-to-r from-brand to-brand-strong" />
        <main className="flex-1 overflow-auto p-8">{children}</main>
      </div>
    </div>
  )
}
