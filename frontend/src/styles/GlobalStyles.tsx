import { Global } from '@emotion/react'
import React from 'react'
import tw, { GlobalStyles as BaseStyles, css } from 'twin.macro'

const customStyles = css({
  html: {
    ...tw`h-full bg-gray-300`,
  },
  body: {
    ...tw`antialiased`,
    ...tw`h-full`,
  },
  'body > #root > div': {
    height: '100%',
    minHeight: '100vh',
    margin: '0 auto',
  },
})

const GlobalStyles = () => (
  <React.Fragment>
    <BaseStyles />
    <Global styles={customStyles} />
  </React.Fragment>
)

export default GlobalStyles
