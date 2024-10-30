import { Suspense } from 'react'

import { IconMapping } from './constants'
import type { IconType } from './types'

interface IconProps {
  icon: IconType
}

export const Icon = ({ icon }: IconProps): JSX.Element | null => {
  const Element = IconMapping[icon]

  return (
    <Suspense fallback={null}>
      <Element className='tek-size-4 tek-text-white' />
    </Suspense>
  )
}
