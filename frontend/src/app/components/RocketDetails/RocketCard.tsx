import Image from 'next/image'
import { memo } from 'react'

import type { Rocket } from '@/app/types'

interface RocketCardProps {
  rocket: Rocket
  onOpenModal: (rocket: Rocket) => void
}

function RocketCard({
  rocket,
  onOpenModal,
}: RocketCardProps): React.ReactElement {
  const getImageUrl = (rocket: Rocket): string => {
    return rocket.flickr_images?.[0] || '/images/placeholder.svg'
  }

  return (
    <div
      className="rocket-card border border-border rounded-lg p-4 bg-card hover:bg-card/90 transition-colors duration-300 opacity-0"
      data-testid="rocket-card"
    >
      <div
        className="relative w-full aspect-video mb-4"
        data-testid="rocket-image-container"
      >
        <Image
          src={getImageUrl(rocket) || '/images/placeholder.svg'}
          alt={`Image of ${rocket.name}`}
          fill
          sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          className="object-cover rounded-lg"
          placeholder="blur"
          blurDataURL={`data:image/svg+xml;base64,${btoa(
            '<svg xmlns="http://www.w3.org/2000/svg" width="400" height="300" viewBox="0 0 400 300"><rect width="400" height="300" fill="#1F2937"/></svg>'
          )}`}
          data-testid="rocket-image"
        />
      </div>
      <h3
        className="text-xl font-semibold mb-2 text-foreground"
        data-testid="rocket-name"
      >
        {rocket.name}
      </h3>
      <dl className="space-y-2" data-testid="rocket-details">
        <RocketDetail
          label="Height"
          value={`${rocket.height.meters}m / ${rocket.height.feet}ft`}
        />
        <RocketDetail
          label="Diameter"
          value={`${rocket.diameter.meters}m / ${rocket.diameter.feet}ft`}
        />
        <RocketDetail
          label="Mass"
          value={`${rocket.mass.kg.toLocaleString()}kg / ${rocket.mass.lb.toLocaleString()}lb`}
        />
        <RocketDetail label="First Flight" value={rocket.first_flight} />
        <RocketDetail
          label="Status"
          value={rocket.active ? 'Active' : 'Inactive'}
          valueClassName={rocket.active ? 'text-green-400' : 'text-red-400'}
        />
      </dl>
      <button
        className="mt-4 btn btn-primary w-full focus:ring-2 focus:ring-primary focus:ring-offset-2 focus:ring-offset-background"
        onClick={() => onOpenModal(rocket)}
        aria-label={`View details for ${rocket.name}`}
        data-testid="view-details-button"
      >
        View Details
      </button>
    </div>
  )
}

interface RocketDetailProps {
  label: string
  value: string
  valueClassName?: string
}

const RocketDetail = memo(
  ({
    label,
    value,
    valueClassName = 'text-foreground',
  }: RocketDetailProps): React.ReactElement => (
    <div data-testid={`rocket-detail-${label}`}>
      <dt className="text-base text-muted-foreground inline">{label}: </dt>
      <dd className={`inline ${valueClassName}`}>{value}</dd>
    </div>
  )
)

RocketDetail.displayName = 'RocketDetail'

export default memo(RocketCard)
