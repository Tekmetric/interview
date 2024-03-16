import tw, { styled } from 'twin.macro'

const Container = styled.div(({ variant }: Pick<AvatarProps, 'variant'>) => [
  tw`flex flex-row gap-[8px] items-center`,
  variant === 'left' && tw`flex-row-reverse justify-end`,
])

type AvatarProps = {
  size?: number
  image: string
  name: string
  variant: 'left' | 'right'
}

const Avatar = ({ variant, size, image, name }: AvatarProps): JSX.Element => {
  return (
    <Container variant={variant}>
      <span tw="text-[14px] font-semibold">{name}</span>
      <img tw="rounded-full object-cover" css={{ width: `${size ?? 24}px`, height: `${size ?? 24}px` }} src={image} />
    </Container>
  )
}

export default Avatar
