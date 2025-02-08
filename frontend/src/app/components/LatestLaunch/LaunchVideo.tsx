import { memo } from 'react'

interface LaunchVideoProps {
  youtubeId: string | undefined
  launchName: string
}

function LaunchVideo({
  youtubeId,
  launchName,
}: LaunchVideoProps): React.ReactElement | null {
  if (!youtubeId) return null

  return (
    <div
      className="w-full aspect-video mb-6 mx-auto max-w-2xl px-4 sm:px-0"
      data-testid="launch-video-container"
    >
      <iframe
        src={`https://www.youtube.com/embed/${youtubeId}`}
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowFullScreen
        className="w-full h-full rounded-lg shadow-lg"
        loading="lazy"
        title={`${launchName} Launch Video`}
        data-testid="launch-video-iframe"
      />
    </div>
  )
}

export default memo(LaunchVideo)
