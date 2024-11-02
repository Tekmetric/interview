import { Suspense } from 'react'

import { IconMapping } from './constants'
import type { IconType } from './types'

interface IconProps {
  icon: IconType
}

export const Icon = ({ icon }: IconProps): JSX.Element | null => {
  const Element = IconMapping[icon]

  return (
    <Suspense
      fallback={
        <div className='tek-h-4 tek-w-4 tek-rounded-full tek-bg-slate-800/50' />
      }
    >
      <Element className='tek-size-4 tek-text-current' />
    </Suspense>
  )
}
