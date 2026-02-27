import type { Meta, StoryObj } from '@storybook/nextjs-vite'
import { Sidebar } from '@/components/Sidebar/Sidebar'

const meta = {
  title: 'Components/Sidebar',
  component: Sidebar,
  parameters: {
    layout: 'fullscreen',
    nextjs: {
      appDirectory: true,
      navigation: { pathname: '/dashboard' },
    },
  },
} satisfies Meta<typeof Sidebar>

export default meta

type Story = StoryObj<typeof meta>

export const DashboardActive: Story = {}

export const RepairOrdersNestedActive: Story = {
  parameters: {
    nextjs: {
      appDirectory: true,
      navigation: { pathname: '/repair-orders/RO-1023' },
    },
  },
}
