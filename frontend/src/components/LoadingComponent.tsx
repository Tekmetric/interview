import React, { ReactElement } from 'react'

type Props = {
  loadingText?: string
}

const LoadingComponent = ({ loadingText }: Props): ReactElement => {
  const displayedText = loadingText ?? 'Loading...'

  return <h1 className='w-full text-center'>{displayedText}</h1>
}

export default LoadingComponent
