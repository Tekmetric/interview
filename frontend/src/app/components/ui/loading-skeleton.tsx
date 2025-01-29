interface LoadingSkeletonProps {
  className?: string
}

export function LoadingSkeleton({
  className = 'h-40',
}: LoadingSkeletonProps): React.ReactElement {
  return (
    <div
      data-testid="loading-skeleton"
      className={`bg-card/50 animate-pulse rounded-lg flex items-center justify-center ${className}`}
    >
      <div className="text-muted-foreground">Loading...</div>
    </div>
  )
}
