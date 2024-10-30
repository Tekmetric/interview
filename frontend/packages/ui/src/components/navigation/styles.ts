import classNames from 'classnames'

export const NavigationClassNames = classNames(
  'tek-absolute tek-left-4 tek-right-4 tek-top-14 tek-z-10 sm:tek-relative sm:tek-top-0',
  'tek-opacity-0 sm:tek-opacity-100',
  'tek-flex tek-flex-col tek-items-center tek-justify-between tek-gap-2 sm:tek-flex-row',
  'sm:tek-w-full sm:tek-max-w-[512px]',
  'tek-mx-auto',
  'tek-bg-slate-800 tek-backdrop-blur-sm',
  'tek-text-slate-100',
  'tek-px-5 tek-py-2',
  'tek-rounded-lg sm:tek-rounded-full',
  'peer-focus:tek-opacity-100'
)

export const NavigationListClassNames = classNames(
  'tek-flex tek-flex-col tek-items-center tek-gap-2 sm:tek-flex-row'
)
