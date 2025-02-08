import { NavLink } from '../NavLink'

export default {
  title: 'Components/NavLink',
  component: NavLink,
}

export const Default = () => (
  <NavLink
    href="#next-launch"
    onClick={() => {}}
    className="hover:text-primary"
  >
    Next Launch
  </NavLink>
)
