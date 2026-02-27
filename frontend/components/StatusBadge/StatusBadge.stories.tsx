import type { Meta, StoryObj } from '@storybook/nextjs-vite'
import { StatusBadge } from '@/components/StatusBadge/StatusBadge'

const meta = {
  title: 'Components/StatusBadge',
  component: StatusBadge,
} satisfies Meta<typeof StatusBadge>

export default meta

type Story = StoryObj<typeof meta>

export const New: Story = {
  args: { status: 'new' },
}

export const InProgress: Story = {
  args: { status: 'in_progress' },
}

export const WaitingParts: Story = {
  args: { status: 'waiting_parts' },
}

export const Completed: Story = {
  args: { status: 'completed' },
}
