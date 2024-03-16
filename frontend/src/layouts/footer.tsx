import { Icon } from '@iconify/react'
import tw, { styled } from 'twin.macro'

const Container = styled.footer({
  ...tw`w-full min-h-[88px] p-[16px]`,
  ...tw`flex flex-col lg:flex-row gap-[16px] items-center justify-between`,
  ...tw`text-white bg-slate-900`,
})

type FooterProps = {
  socialMedias: { link: string; icon: string }[]
}

const Footer = ({ socialMedias }: FooterProps): JSX.Element => {
  return (
    <Container>
      <span>© 2024 Recipes. All rights reserved.</span>
      <div tw="flex flex-row justify-end items-center gap-[8px]">
        {socialMedias.map(social => (
          <a key={social.link} href={social.link} target="_blank" rel="noreferrer">
            <Icon icon={social.icon} color="white" width={24} />
          </a>
        ))}
      </div>
    </Container>
  )
}

export default Footer
