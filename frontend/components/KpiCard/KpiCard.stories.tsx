import { CircleCheckBig, Clock3 } from 'lucide-react'
import type { Meta, StoryObj } from '@storybook/nextjs-vite'
import { KpiCard } from '@/components/KpiCard/KpiCard'

const meta = {
  title: 'Components/UI/KpiCard',
  component: KpiCard,
  args: {
    title: 'Open Orders',
    value: '24',
    icon: <Clock3 className="h-5 w-5" />,
    iconClass: 'bg-brand-100 text-brand-700',
  },
} satisfies Meta<typeof KpiCard>

export default meta

type Story = StoryObj<typeof meta>

export const Default: Story = {}

export const Success: Story = {
  args: {
    title: 'Completion Rate',
    value: '89%',
    icon: <CircleCheckBig className="h-5 w-5" />,
    iconClass: 'bg-green-100 text-green-700',
    valueClass: 'text-green-700',
  },
}
