import type { Metadata } from 'next'

interface PageMetadataProps {
  title: string
  description: string
  imageUrl?: string
}

export function generatePageMetadata({
  title,
  description,
  imageUrl,
}: PageMetadataProps): Metadata {
  return {
    title,
    description,
    openGraph: {
      title,
      description,
      images: imageUrl
        ? [
            {
              url: imageUrl,
              width: 300,
              height: 300,
              alt: title,
            },
          ]
        : [],
    },
    twitter: {
      card: 'summary_large_image',
      title,
      description,
      images: imageUrl ? [imageUrl] : [],
    },
  }
}
