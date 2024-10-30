import { Icon } from '../../../icon/icon'
import { NavigationButtonClassNames } from './styles'

export const NavigationButton = (): JSX.Element => (
  <button tabIndex={0} className={NavigationButtonClassNames}>
    <span className='tek-sr-only'>Open main menu</span>
    <Icon icon='close' />
  </button>
)
