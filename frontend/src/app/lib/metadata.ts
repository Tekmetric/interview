import type { Metadata } from 'next'

export function generatePageMetadata({
  title,
  description,
  imageUrl,
}: {
  title: string
  description: string
  imageUrl: string
}): Metadata {
  const baseUrl =
    process.env.BASE_URL || 'https://tekmetric-interview.vercel.app/'
  return {
    title,
    description,
    metadataBase: new URL(baseUrl),
    openGraph: {
      title,
      description,
      url: new URL(baseUrl),
      images: [{ url: imageUrl }],
    },
    twitter: {
      card: 'summary_large_image',
      title,
      description,
      images: [imageUrl],
    },
  }
}
