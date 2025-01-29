interface LaunchDetailsProps {
  details: string | undefined
}

function LaunchDetails({ details }: LaunchDetailsProps): React.ReactNode {
  if (!details) return null

  return (
    <div
      className="bg-muted/50 rounded-lg p-4"
      data-testid="latest-launch-details"
    >
      <h3
        className="text-lg font-medium mb-2 text-foreground"
        data-testid="mission-details-title"
      >
        Mission Details
      </h3>
      <p
        className="text-sm text-muted-foreground leading-relaxed"
        data-testid="mission-details-text"
      >
        {details}
      </p>
    </div>
  )
}

export default LaunchDetails
