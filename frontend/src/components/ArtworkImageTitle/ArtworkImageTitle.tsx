import { StyledArtworkImageTitle } from './styled'

type Props = {
  title: string
  date: string | null
  artist: string | null
}

export const ArtworkImageTitle = ({ title, date, artist }: Props) => {
  return (
    <StyledArtworkImageTitle>
      "{title}"
      {artist && (
        <>
          {' by '}
          {artist}
        </>
      )}
      {date && <>, {date}</>}
    </StyledArtworkImageTitle>
  )
}
