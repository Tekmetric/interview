import { lazy } from 'react'

import type { IconType } from './types'

const ArrowRightStartOnRectangleIcon = lazy(
  () => import('@heroicons/react/24/outline/ArrowRightStartOnRectangleIcon')
)

const XMarkIcon = lazy(() => import('@heroicons/react/24/outline/XMarkIcon'))
const Bars3Icon = lazy(() => import('@heroicons/react/24/outline/Bars3Icon'))

export const IconMapping: Record<IconType, React.ElementType> = {
  close: XMarkIcon,
  logout: ArrowRightStartOnRectangleIcon,
  menu: Bars3Icon
}
