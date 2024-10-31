import classNames from 'classnames'

import type { ButtonSize, ButtonVariant, ButtonWidth } from './types'

const ButtonWidthClassNames: Record<ButtonWidth, string> = {
  default: '',
  full: 'tek-w-full'
}

const ButtonSizeClassNames: Record<ButtonSize, string> = {
  small: classNames('tek-px-2 tek-py-1', 'tek-text-sm'),
  medium: classNames('tek-px-4 tek-py-4', 'tek-text-sm')
}

const ButtonVariantClassNames: Record<ButtonVariant, string> = {
  primary: classNames(
    'tek-bg-slate-900',
    'tek-text-slate-50',
    'enabled:hover:tek-bg-slate-700',
    'enabled:active:tek-bg-slate-900',
    'enabled:focus:tek-ring-slate-500'
  ),
  secondary: classNames(
    'tek-bg-slate-100',
    'tek-text-slate-800',
    'tek-border tek-border-slate-300',
    'enabled:hover:tek-bg-slate-200',
    'enabled:active:tek-bg-slate-100',
    'enabled:focus:tek-ring-slate-200'
  )
}

export const ButtonClassNames = ({
  width = 'default',
  size = 'medium',
  variant = 'primary'
}: {
  width?: ButtonWidth
  size?: ButtonSize
  variant?: ButtonVariant
} = {}): string =>
  classNames(
    ButtonWidthClassNames[width],
    ButtonSizeClassNames[size],
    ButtonVariantClassNames[variant],
    'tek-flex tek-items-center tek-justify-center tek-gap-1',
    'tek-rounded-xl',
    'enabled:focus:tek-outline-none enabled:focus:tek-ring-2',
    'disabled:tek-cursor-not-allowed disabled:tek-select-none',
    'disabled:tek-opacity-50'
  )
