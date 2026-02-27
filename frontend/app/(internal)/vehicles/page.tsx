'use client'

import { $api } from '@/lib/api/client/api-client'
import { PageTitle } from '@/components/PageTitle/PageTitle'
import { Table } from '@/ui/Table/Table'

const columns = [
  { key: 'vehicle', label: 'Year / Make / Model' },
  { key: 'vin', label: 'VIN' },
  { key: 'mileage', label: 'Mileage' },
  { key: 'customerId', label: 'Customer ID' },
]

export default function VehiclesPage() {
  const { data, isLoading, isError } = $api.useQuery('get', '/api/v1/vehicles')

  return (
    <div>
      <PageTitle title="Vehicles" />
      <Table.Card>
        <Table.Root columns={columns}>
          <Table.Header>
            <Table.HeaderRow />
          </Table.Header>
          <Table.Body>
            {isLoading && <Table.SkeletonRows count={3} />}

            {isError && <Table.ErrorRow message="Failed to load vehicles." />}

            {data?.map((vehicle) => (
              <Table.Row key={vehicle.id} className="transition-colors hover:bg-brand-50/50">
                <Table.Cell className="font-medium text-foreground">
                  {vehicle.year} {vehicle.make} {vehicle.model}
                </Table.Cell>
                <Table.Cell className="font-mono text-xs text-slate-600">{vehicle.vin}</Table.Cell>
                <Table.Cell className="text-slate-600">
                  {vehicle.mileage.toLocaleString('en-US')} mi
                </Table.Cell>
                <Table.Cell className="font-mono text-xs text-slate-500">
                  {vehicle.customerId}
                </Table.Cell>
              </Table.Row>
            ))}
          </Table.Body>
        </Table.Root>
      </Table.Card>
    </div>
  )
}
