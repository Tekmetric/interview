import classNames from 'classnames'

import type { CardVariant } from './types'

const CardVariantClassNames: Record<CardVariant, string> = {
  primary: 'tek-bg-white',
  secondary: 'tek-bg-gray-100'
}

export const CardClassNames = ({
  variant = 'primary'
}: {
  variant?: CardVariant
}): string =>
  classNames(
    CardVariantClassNames[variant],
    'tek-flex tek-flex-col tek-gap-1',
    'tek-p-4',
    'tek-border-slate-300',
    'tek-shadow-lg',
    'tek-border',
    'tek-rounded-lg'
  )
