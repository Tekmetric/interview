import type { Meta, StoryObj } from '@storybook/nextjs-vite'
import { StatusBadge } from '@/components/StatusBadge/StatusBadge'
import { Table } from '@/ui/Table/Table'

const columns = [
  { key: 'id', label: 'RO #' },
  { key: 'customer', label: 'Customer' },
  { key: 'status', label: 'Status' },
  { key: 'total', label: 'Total', align: 'right' as const },
]

const rows = [
  { id: 'RO-1023', customer: 'Maya Chen', status: 'in_progress' as const, total: '$482.00' },
  { id: 'RO-1024', customer: 'Luis Ortega', status: 'waiting_parts' as const, total: '$125.50' },
  { id: 'RO-1025', customer: 'Skyler Park', status: 'completed' as const, total: '$940.25' },
]

const meta = {
  title: 'Components/UI/Table',
} satisfies Meta

export default meta

type Story = StoryObj

export const Default: Story = {
  render: () => (
    <Table.Card>
      <Table.Root columns={columns}>
        <Table.Header>
          <Table.HeaderRow />
        </Table.Header>
        <Table.Body>
          {rows.map((row) => (
            <Table.Row key={row.id}>
              <Table.Cell>{row.id}</Table.Cell>
              <Table.Cell>{row.customer}</Table.Cell>
              <Table.Cell>
                <StatusBadge status={row.status} />
              </Table.Cell>
              <Table.Cell align="right">{row.total}</Table.Cell>
            </Table.Row>
          ))}
        </Table.Body>
      </Table.Root>
    </Table.Card>
  ),
}

export const Loading: Story = {
  render: () => (
    <Table.Card>
      <Table.Root columns={columns}>
        <Table.Header>
          <Table.HeaderRow />
        </Table.Header>
        <Table.Body>
          <Table.SkeletonRows count={4} />
        </Table.Body>
      </Table.Root>
    </Table.Card>
  ),
}

export const ErrorState: Story = {
  render: () => (
    <Table.Card>
      <Table.Root columns={columns}>
        <Table.Header>
          <Table.HeaderRow />
        </Table.Header>
        <Table.Body>
          <Table.ErrorRow message="Unable to load repair orders." />
        </Table.Body>
      </Table.Root>
    </Table.Card>
  ),
}
