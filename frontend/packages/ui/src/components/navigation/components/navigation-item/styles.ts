import classNames from 'classnames'

export const NavigationItemClassNames = ({
  active = false
}: {
  active?: boolean
}): string =>
  classNames(
    '[&_a]:tek-block',
    '[&_a]:tek-px-2 [&_a]:tek-py-2 sm:[&_a]:tek-py-1',
    '[&_a]:tek-rounded-lg',
    '[&_a]:hover:tek-bg-slate-600',
    '[&_a]:tek-select-none',
    'tek-text-sm',
    !active && '[&_a]:tek-bg-transparent',
    active &&
      classNames('[&_a]:tek-bg-slate-700', '[&_a]:tek-pointer-events-none')
  )
