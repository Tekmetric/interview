import { AdditionalInfoCloseButton } from '../AdditionalInfoCloseButton/AdditionalInfoCloseButton'
import { StyledArtworkAdditionalInfo } from './styles'

type Props = {
  visible: boolean
  title: string
  description: string | null
  date: string | null
  artist: string | null
  onClose: () => void
}

const AdditionalInfoItem = ({
  field,
  value
}: {
  field: string | null
  value: string | null
}) => {
  if (!value) {
    return null
  }

  return (
    <span>
      {field && `${field}: `}
      <span dangerouslySetInnerHTML={{ __html: value }} />
    </span>
  )
}

export const ArtworkAdditionalInfo = ({
  visible,
  title,
  description,
  date,
  artist,
  onClose
}: Props) => {
  const handleClick = () => {
    if (!visible) {
      return
    }

    onClose()
  }

  return (
    <StyledArtworkAdditionalInfo $visible={visible}>
      <AdditionalInfoCloseButton onClick={handleClick} />

      <AdditionalInfoItem field='Title' value={`"${title}"`} />

      <AdditionalInfoItem field='Artist' value={artist} />

      <AdditionalInfoItem field='Date' value={date} />

      <AdditionalInfoItem field={null} value={description} />
    </StyledArtworkAdditionalInfo>
  )
}
