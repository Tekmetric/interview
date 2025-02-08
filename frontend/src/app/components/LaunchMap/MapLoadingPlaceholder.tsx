interface MapLoadingPlaceholderProps {
  height: number
}

function MapLoadingPlaceholder({
  height,
}: MapLoadingPlaceholderProps): React.ReactElement {
  return (
    <div
      className="bg-muted animate-pulse rounded-lg flex items-center justify-center"
      style={{ height: `${height}px` }}
      aria-label="Map loading"
      data-testid="map-loading-placeholder"
    >
      <p className="text-muted-foreground">Loading map...</p>
    </div>
  )
}

export default MapLoadingPlaceholder
