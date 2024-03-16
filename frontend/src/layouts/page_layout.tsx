import { PropsWithChildren } from 'react'
import tw, { styled } from 'twin.macro'
import Footer from './footer'
import Header from './header'

const PageLayoutContainer = styled.div({
  ...tw`h-screen w-full relative`,
  ...tw`flex flex-col max-w-[1200px] bg-white`,
  ...tw`lg:(shadow-xl shadow-gray-500)`,
})

const PageLayout = ({ children }: PropsWithChildren): JSX.Element => {
  return (
    <PageLayoutContainer>
      <Header sticky />
      {children}
      <Footer
        socialMedias={[
          { link: 'https://facebook.com', icon: 'mdi:facebook' },
          { link: 'https://twitter.com', icon: 'mdi:twitter' },
          { link: 'https://linkedin.com', icon: 'mdi:linkedin' },
        ]}
      />
    </PageLayoutContainer>
  )
}

export default PageLayout
