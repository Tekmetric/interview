import type { Meta, StoryObj } from '@storybook/nextjs-vite'
import { BrandMark } from '@/components/BrandMark/BrandMark'

const meta = {
  title: 'Components/Branding/BrandMark',
  component: BrandMark,
  args: {
    showText: true,
  },
  parameters: {
    backgrounds: {
      default: 'dark',
      values: [
        { name: 'dark', value: '#171a21' },
        { name: 'light', value: '#f7f8fa' },
      ],
    },
  },
} satisfies Meta<typeof BrandMark>

export default meta

type Story = StoryObj<typeof meta>

export const WithText: Story = {}

export const IconOnly: Story = {
  args: {
    showText: false,
  },
}
