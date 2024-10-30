import classNames from 'classnames'

import { Icon } from '../../../icon/icon'
import { NavigationButtonClassNames } from './styles'

interface NavigationButtonProps {
  open?: boolean
  onClick?: () => void
}

export const NavigationButton = ({
  open,
  onClick
}: NavigationButtonProps): JSX.Element => (
  <button tabIndex={0} className={NavigationButtonClassNames} onClick={onClick}>
    <span className='tek-sr-only'>Open main menu</span>

    <span className={classNames(open && 'tek-hidden')}>
      <Icon icon='menu' />
    </span>

    <span className={classNames(!open && 'tek-hidden')}>
      <Icon icon='close' />
    </span>
  </button>
)
