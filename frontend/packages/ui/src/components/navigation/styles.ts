import classNames from 'classnames'

export const NavigationClassNames = ({
  open = false
}: {
  open?: boolean
}): string =>
  classNames(
    'tek-absolute tek-left-4 tek-right-4 tek-top-14 tek-z-10 sm:tek-relative sm:tek-top-0',
    'tek-flex-col tek-gap-4 sm:tek-flex sm:tek-flex-row sm:tek-items-center sm:tek-justify-between sm:tek-gap-2',
    'sm:tek-w-full sm:tek-max-w-[512px]',
    'tek-mx-auto',
    'tek-bg-slate-800 tek-backdrop-blur-sm',
    'tek-text-slate-100',
    'tek-px-5 tek-py-4 sm:tek-py-2',
    'tek-rounded-lg sm:tek-rounded-full',
    !open && 'tek-hidden',
    Boolean(open) && 'tek-flex'
  )

export const NavigationListClassNames = classNames(
  'tek-flex tek-flex-col tek-gap-2 sm:tek-flex-row sm:tek-items-center',
  'tek-pb-4 sm:tek-pb-0',
  'tek-border-b tek-border-slate-700 sm:tek-border-none'
)
