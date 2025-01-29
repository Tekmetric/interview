interface CountdownDisplayProps {
  timeLeft: {
    days: number
    hours: number
    minutes: number
    seconds: number
  }
}

export function CountdownDisplay({
  timeLeft,
}: CountdownDisplayProps): React.ReactElement {
  return (
    <div
      className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8"
      data-testid="countdown-display"
    >
      {Object.entries(timeLeft).map(([unit, value]) => (
        <div
          key={unit}
          className="text-center p-4 bg-muted rounded-lg"
          data-testid={`countdown-${unit}`}
        >
          <div className="text-4xl font-bold text-primary mb-2">
            {typeof value === 'number' && !isNaN(value)
              ? value.toString().padStart(2, '0')
              : '00'}
          </div>
          <div className="text-sm uppercase text-muted-foreground">{unit}</div>
        </div>
      ))}
    </div>
  )
}
