'use client'

import { $api } from '@/lib/api/client/api-client'
import { PageTitle } from '@/components/PageTitle/PageTitle'
import { Table } from '@/ui/Table/Table'

const columns = [
  { key: 'name', label: 'Name' },
  { key: 'phone', label: 'Phone' },
  { key: 'email', label: 'Email' },
  { key: 'id', label: 'ID' },
]

export default function CustomersPage() {
  const { data, isLoading, isError } = $api.useQuery('get', '/api/v1/customers')

  return (
    <div>
      <PageTitle title="Customers" />
      <Table.Card>
        <Table.Root columns={columns}>
          <Table.Header>
            <Table.HeaderRow />
          </Table.Header>
          <Table.Body>
            {isLoading && <Table.SkeletonRows count={3} />}

            {isError && <Table.ErrorRow message="Failed to load customers." />}

            {data?.map((customer) => (
              <Table.Row key={customer.id} className="transition-colors hover:bg-brand-50/50">
                <Table.Cell>
                  <div className="flex items-center gap-3">
                    <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-brand-soft text-sm font-semibold text-brand-700">
                      {customer.name.charAt(0).toUpperCase()}
                    </div>
                    <span className="font-medium text-foreground">{customer.name}</span>
                  </div>
                </Table.Cell>
                <Table.Cell className="text-slate-600">{customer.phone}</Table.Cell>
                <Table.Cell className="text-slate-600">{customer.email}</Table.Cell>
                <Table.Cell className="font-mono text-xs text-slate-500">{customer.id}</Table.Cell>
              </Table.Row>
            ))}
          </Table.Body>
        </Table.Root>
      </Table.Card>
    </div>
  )
}
