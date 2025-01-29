import { MobileMenu } from '../MobileMenu'

export default {
  title: 'Components/MobileMenu',
  component: MobileMenu,
  argTypes: {
    isOpen: { control: 'boolean' },
  },
  parameters: {
    viewport: {
      defaultViewport: 'iphonex',
    },
  },
}

const navItemsExample = [
  { href: '#next-launch', label: 'Next Launch' },
  { href: '#latest-launch', label: 'Latest Launch' },
  { href: '#launches', label: 'Launches' },
  { href: '#rockets', label: 'Rockets' },
]

// TODO: Fix this story
const Template = (args: any) => <MobileMenu {...args} />

export const Default = Template.bind({}) as any
Default.args = {
  isOpen: false,
  navItems: navItemsExample,
  onClose: () => {},
  onNavClick: () => {},
}

export const Open = Template.bind({}) as any
Open.args = {
  isOpen: true,
  navItems: navItemsExample,
  onClose: () => {},
  onNavClick: () => {},
}
