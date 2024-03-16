import { Icon } from '@iconify/react'
import { Link } from 'react-router-dom'
import tw, { styled } from 'twin.macro'

const Container = styled.div(({ sticky }: HeaderProps) => [
  tw`w-full min-h-[88px] p-[16px]`,
  tw`flex flex-row items-center z-50`,
  sticky && tw`sticky top-0`,

  tw`bg-slate-900`,
])

type HeaderProps = {
  sticky?: boolean
}

const Header = ({ sticky }: HeaderProps): JSX.Element => {
  return (
    <Container sticky={sticky}>
      <Link to={'/'} tw="flex flex-row items-start gap-[8px]">
        <Icon icon={'game-icons:hot-meal'} color="white" width={46} />
        <span tw="text-[white] text-[36px] font-bold">Recipes</span>
      </Link>
    </Container>
  )
}

export default Header
