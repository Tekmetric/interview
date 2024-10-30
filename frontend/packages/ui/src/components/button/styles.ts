import classNames from 'classnames'

import type { ButtonSize, ButtonWidth } from './types'

const ButtonWidthClassNames: Record<ButtonWidth, string> = {
  default: '',
  full: 'tek-w-full'
}

const ButtonSizeClassNames: Record<ButtonSize, string> = {
  small: classNames('tek-px-2 tek-py-1', 'tek-text-sm'),
  medium: classNames('tek-px-4 tek-py-4', 'tek-text-sm')
}

export const ButtonClassNames = ({
  width = 'default',
  size = 'medium'
}: { width?: ButtonWidth; size?: ButtonSize } = {}): string =>
  classNames(
    ButtonWidthClassNames[width],
    ButtonSizeClassNames[size],
    'tek-flex tek-items-center tek-justify-center tek-gap-1',
    'tek-bg-slate-900',
    'tek-text-slate-50',
    'tek-rounded-xl',
    'enabled:focus:tek-outline-none enabled:focus:tek-ring-2 enabled:focus:tek-ring-slate-500',
    'enabled:hover:tek-bg-slate-700',
    'enabled:active:tek-bg-slate-900',
    'disabled:tek-cursor-not-allowed disabled:tek-select-none',
    'disabled:tek-opacity-50'
  )
