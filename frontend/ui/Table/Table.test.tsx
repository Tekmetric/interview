import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Table, type TableColumn } from '@/ui/Table/Table'

const columns: TableColumn[] = [
  { key: 'id', label: 'ID' },
  { key: 'customer', label: 'Customer' },
  { key: 'status', label: 'Status' },
]

describe('Table compound component', () => {
  it('renders header cells from columns via HeaderRow', () => {
    render(
      <Table.Root columns={columns}>
        <Table.Header>
          <Table.HeaderRow />
        </Table.Header>
      </Table.Root>,
    )

    expect(screen.getByRole('columnheader', { name: 'ID' })).toBeInTheDocument()
    expect(screen.getByRole('columnheader', { name: 'Customer' })).toBeInTheDocument()
    expect(screen.getByRole('columnheader', { name: 'Status' })).toBeInTheDocument()
  })

  it('renders ErrorRow with a colSpan equal to the column count', () => {
    render(
      <Table.Root columns={columns}>
        <Table.Body>
          <Table.ErrorRow message="Load failed" />
        </Table.Body>
      </Table.Root>,
    )

    const messageCell = screen.getByText('Load failed')
    expect(messageCell).toBeInTheDocument()
    expect(messageCell).toHaveAttribute('colspan', String(columns.length))
  })

  it('renders SkeletonRows with the requested number of rows and one cell per column', () => {
    const rowCount = 2
    const { container } = render(
      <Table.Root columns={columns}>
        <Table.Body>
          <Table.SkeletonRows count={rowCount} />
        </Table.Body>
      </Table.Root>,
    )

    const rows = container.querySelectorAll('tbody tr')
    expect(rows.length).toBe(rowCount)

    rows.forEach((row) => {
      expect(row.querySelectorAll('td').length).toBe(columns.length)
    })
  })

  it('throws when HeaderRow is used outside Table.Root', () => {
    expect(() =>
      render(
        <table>
          <thead>
            <Table.HeaderRow />
          </thead>
        </table>,
      ),
    ).toThrow('Table components must be used within Table.Root')
  })
})
