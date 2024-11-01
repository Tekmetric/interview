import classNames from 'classnames'

import type {
  TypographyAlignment,
  TypographyColor,
  TypographySize,
  TypographyWeight
} from './types'

export const TypographySizeClassNames: Record<TypographySize, string> = {
  xs: 'tek-text-xs',
  sm: 'tek-text-sm',
  md: 'tek-text-md',
  lg: 'tek-text-lg',
  xl: 'tek-text-xl',
  '2xl': 'tek-text-2xl'
}

export const TypographyWeightClassNames: Record<TypographyWeight, string> = {
  medium: 'tek-font-medium',
  semibold: 'tek-font-semibold'
}

export const TypographyAlignmentClassNames: Record<
  TypographyAlignment,
  string
> = {
  left: 'tek-text-left',
  center: 'tek-text-center',
  right: 'tek-text-right'
}

export const TypographyColorClassNames: Record<TypographyColor, string> = {
  current: '',
  'slate-500': 'tek-text-slate-500'
}

export const TypographyClassNames = ({
  alignment = 'left',
  color = 'current',
  size = 'md',
  weight = 'medium'
}: {
  alignment?: TypographyAlignment
  color?: TypographyColor
  size?: TypographySize
  weight?: TypographyWeight
}): string =>
  classNames(
    TypographySizeClassNames[size],
    TypographyWeightClassNames[weight],
    TypographyAlignmentClassNames[alignment],
    TypographyColorClassNames[color],
    'tek-whitespace-pre-line'
  )
