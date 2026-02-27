import type { Meta, StoryObj } from '@storybook/nextjs-vite'
import { PageTitle } from '@/components/PageTitle/PageTitle'

const meta = {
  title: 'Components/UI/PageTitle',
  component: PageTitle,
  args: {
    title: 'Dashboard',
  },
} satisfies Meta<typeof PageTitle>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {}

export const LongTitle: Story = {
  args: {
    title: 'Repair Orders Overview For Northeast Region',
  },
}
