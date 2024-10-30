import classNames from 'classnames'

import type {
  TypographyAlignment,
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

export const TypographyClassNames = ({
  alignment = 'left',
  size = 'md',
  weight = 'medium'
}: {
  alignment?: TypographyAlignment
  size?: TypographySize
  weight?: TypographyWeight
}): string =>
  classNames(
    TypographySizeClassNames[size],
    TypographyWeightClassNames[weight],
    TypographyAlignmentClassNames[alignment]
  )
