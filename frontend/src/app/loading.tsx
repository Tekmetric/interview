import { LoadingSkeleton } from '@/app/components/ui/loading-skeleton'

export default function Loading(): React.ReactElement {
  return (
    <div className="space-y-8">
      <LoadingSkeleton className="h-40" />
      <LoadingSkeleton className="h-96" />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <LoadingSkeleton className="h-80" />
        <LoadingSkeleton className="h-80" />
      </div>
      <LoadingSkeleton className="h-80" />
      <LoadingSkeleton className="h-80" />
    </div>
  )
}
