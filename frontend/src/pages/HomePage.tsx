import { useState } from 'react';
import { Layout } from '../components/layout';
import { RepairServicesTable } from '../components/RepairServicesTable';
import { useFetchRepairServices } from '../hooks/useFetchRepairServices';
import { SortingState } from '@tanstack/react-table';
import { AddNewServiceButton } from '../components/AddNewServiceButton';

export const HomePage = () => {
  const [pageIndex, setPageIndex] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [sorting, setSorting] = useState<SortingState>([]);

  const sortBy = sorting.length > 0 ? sorting[0].id : undefined;
  const sortDirection = sorting.length > 0 ? (sorting[0].desc ? 'desc' : 'asc') : undefined;

  const { data, pageInfo, isLoading, error, mutate } = useFetchRepairServices(
    pageIndex,
    pageSize,
    sortBy,
    sortDirection
  );

  const handlePageChange = (newPageIndex: number, newPageSize: number) => {
    setPageIndex(newPageIndex);
    if (newPageSize !== pageSize) {
      setPageSize(newPageSize);
    }
  };

  const handleSortingChange = (newSorting: SortingState) => {
    setSorting(newSorting);
    // Reset to first page when sorting changes
    setPageIndex(0);
  };

  return (
    <Layout>
      <div className="bg-white p-6 rounded-lg shadow-md">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-2xl font-bold">Repair Services</h1>
          <AddNewServiceButton onServiceAdded={() => mutate()} />
        </div>

        <RepairServicesTable
          data={data}
          pageInfo={pageInfo}
          isLoading={isLoading}
          error={error || null}
          onPageChange={handlePageChange}
          sorting={sorting}
          onSortingChange={handleSortingChange}
          onServiceUpdated={() => mutate()}
        />
      </div>
    </Layout>
  );
};
