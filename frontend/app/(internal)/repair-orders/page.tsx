'use client'

import { $api } from '@/lib/api/client/api-client'
import { StatusBadge } from '@/components/StatusBadge/StatusBadge'
import { PageTitle } from '@/components/PageTitle/PageTitle'
import { Table } from '@/ui/Table/Table'
import { formatUSD } from '@/lib/format/currency'
import { formatDateShortUS } from '@/lib/format/date'

const columns = [
  { key: 'id', label: 'RO #' },
  { key: 'status', label: 'Status' },
  { key: 'openedAt', label: 'Opened' },
  { key: 'labor', label: 'Labor', align: 'right' as const },
  { key: 'parts', label: 'Parts', align: 'right' as const },
  { key: 'tax', label: 'Tax', align: 'right' as const },
  { key: 'total', label: 'Grand Total', align: 'right' as const },
]

export default function RepairOrdersPage() {
  const { data, isLoading, isError } = $api.useQuery('get', '/api/v1/repair-orders')

  return (
    <div>
      <PageTitle title="Repair Orders" />
      <Table.Card scrollX>
        <Table.Root columns={columns}>
          <Table.Header>
            <Table.HeaderRow />
          </Table.Header>
          <Table.Body>
            {isLoading && <Table.SkeletonRows count={3} />}

            {isError && <Table.ErrorRow message="Failed to load repair orders." />}

            {data?.map((ro) => (
              <Table.Row
                key={ro.id}
                className="cursor-pointer transition-colors hover:bg-brand-50/50"
              >
                <Table.Cell className="font-mono text-xs font-semibold text-slate-700">
                  #{ro.id.slice(0, 8).toUpperCase()}
                </Table.Cell>
                <Table.Cell>
                  <StatusBadge status={ro.status} />
                </Table.Cell>
                <Table.Cell className="text-slate-600">{formatDateShortUS(ro.openedAt)}</Table.Cell>
                <Table.Cell align="right" className="text-slate-700">
                  {formatUSD(ro.laborTotal)}
                </Table.Cell>
                <Table.Cell align="right" className="text-slate-700">
                  {formatUSD(ro.partsTotal)}
                </Table.Cell>
                <Table.Cell align="right" className="text-slate-700">
                  {formatUSD(ro.tax)}
                </Table.Cell>
                <Table.Cell align="right" className="font-semibold text-foreground">
                  {formatUSD(ro.grandTotal)}
                </Table.Cell>
              </Table.Row>
            ))}
          </Table.Body>
        </Table.Root>
      </Table.Card>
    </div>
  )
}
