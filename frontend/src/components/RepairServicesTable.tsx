import { useMemo, useState } from 'react';
import {
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  useReactTable,
  getSortedRowModel,
  SortingState,
  PaginationState,
} from '@tanstack/react-table';
import { RepairService } from '../types/api';
import { StatusBadge } from './StatusBadge';
import { ErrorIcon, EditIcon, DeleteIcon } from './svg';
import { PaginationButton } from './PaginationButton';
import { Modal } from './Modal';
import { NewServiceRequestForm } from './NewServiceRequestForm';
import { ConfirmationModal } from './ConfirmationModal';
import { useUpdateRepairService } from '../hooks/useUpdateRepairService';
import { useDeleteRepairService } from '../hooks/useDeleteRepairService';

const columnHelper = createColumnHelper<RepairService>();

type RepairServicesTableProps = {
  data: RepairService[];
  pageInfo: {
    currentPage: number;
    totalPages: number;
    totalItems: number;
    pageSize: number;
  } | null;
  isLoading: boolean;
  error: Error | null;
  onPageChange: (pageIndex: number, pageSize: number) => void;
  sorting: SortingState;
  onSortingChange: (sorting: SortingState) => void;
  onServiceUpdated: () => void;
};

export const RepairServicesTable = ({
  data,
  pageInfo,
  isLoading,
  error,
  onPageChange,
  sorting,
  onSortingChange,
  onServiceUpdated,
}: RepairServicesTableProps) => {
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [serviceToEdit, setServiceToEdit] = useState<RepairService | null>(null);

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [serviceToDelete, setServiceToDelete] = useState<RepairService | null>(null);

  const { updateService, error: updateError } = useUpdateRepairService();
  const { deleteService, error: deleteError, isLoading: isDeleting } = useDeleteRepairService();

  const handleEditClick = (service: RepairService) => {
    setServiceToEdit(service);
    setIsEditModalOpen(true);
  };

  const handleDeleteClick = (service: RepairService) => {
    setServiceToDelete(service);
    setIsDeleteModalOpen(true);
  };

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false);
    setServiceToEdit(null);
  };

  const handleCloseDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setServiceToDelete(null);
  };

  const handleUpdateService = async (formData: any) => {
    if (!serviceToEdit) return;

    try {
      const serviceData = {
        customerName: formData.customerName,
        customerPhone: formData.customerPhone,
        vehicleMake: formData.vehicleMake,
        vehicleModel: formData.vehicleModel,
        vehicleYear: Number(formData.vehicleYear),
        licensePlate: formData.licensePlate,
        serviceDescription: formData.serviceDescription || '',
        odometerReading: Number(formData.odometerReading),
        status: formData.status,
      };

      await updateService(serviceToEdit.id, serviceData);
      setIsEditModalOpen(false);
      onServiceUpdated(); // Refresh the list after updating
    } catch (err) {
      // Error is handled by the hook
    }
  };

  const handleDeleteService = async () => {
    if (!serviceToDelete) return;

    await deleteService(serviceToDelete.id);
    setIsDeleteModalOpen(false);
    onServiceUpdated();
  };
  const columns = useMemo(
    () => [
      columnHelper.accessor('id', {
        header: 'ID',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('customerName', {
        header: 'Customer Name',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('customerPhone', {
        header: 'Phone',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('vehicleMake', {
        header: 'Make',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('vehicleModel', {
        header: 'Model',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('vehicleYear', {
        header: 'Year',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('licensePlate', {
        header: 'License Plate',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('serviceDescription', {
        header: 'Service Description',
        cell: info => info.getValue(),
      }),
      columnHelper.accessor('odometerReading', {
        header: 'Odometer',
        cell: info => info.getValue().toLocaleString(),
      }),
      columnHelper.accessor('status', {
        header: 'Status',
        cell: info => <StatusBadge status={info.getValue()} />,
      }),
      columnHelper.display({
        id: 'actions',
        header: 'Actions',
        cell: props => {
          const service = props.row.original;
          return (
            <div className="flex space-x-4">
              <button
                onClick={() => handleEditClick(service)}
                className="text-blue-600 hover:text-blue-800 p-1 rounded hover:bg-blue-50 transition-colors"
                title="Edit service"
                aria-label="Edit service"
              >
                <EditIcon />
              </button>
              <button
                onClick={() => handleDeleteClick(service)}
                className="text-red-600 hover:text-red-800 p-1 rounded hover:bg-red-50 transition-colors"
                title="Delete service"
                aria-label="Delete service"
              >
                <DeleteIcon />
              </button>
            </div>
          );
        },
      }),
    ],
    []
  );

  const pagination: PaginationState = {
    pageIndex: pageInfo?.currentPage || 0,
    pageSize: pageInfo?.pageSize || 10,
  };

  const table = useReactTable({
    data: data || [],
    columns,
    pageCount: pageInfo?.totalPages || 1,
    state: {
      sorting,
      pagination,
    },
    onSortingChange: updater => {
      const newSorting = typeof updater === 'function' ? updater(sorting) : updater;
      onSortingChange(newSorting);
    },
    onPaginationChange: newPagination => {
      const updatedPagination =
        typeof newPagination === 'function' ? newPagination(pagination) : newPagination;
      onPageChange(updatedPagination.pageIndex, updatedPagination.pageSize);
    },
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    manualPagination: true,
    autoResetPageIndex: false,
  });

  if (error) {
    return (
      <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-4">
        <div className="flex">
          <div className="flex-shrink-0">
            <ErrorIcon />
          </div>
          <div className="ml-3">
            <p className="text-sm text-red-700">
              Error loading repair services: {error?.message || 'Unknown error'}
            </p>
          </div>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="w-full h-64 flex items-center justify-center">
        <div className="flex flex-col items-center">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mb-4"></div>
          <p className="text-gray-500">Loading repair services...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col">
      <div className="overflow-x-auto">
        <div className="inline-block min-w-full align-middle">
          <div className="overflow-hidden shadow-sm ring-1 ring-black ring-opacity-5">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                {table.getHeaderGroups().map(headerGroup => (
                  <tr key={headerGroup.id}>
                    {headerGroup.headers.map(header => (
                      <th
                        key={header.id}
                        className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer"
                        onClick={header.column.getToggleSortingHandler()}
                      >
                        <div className="flex items-center">
                          {flexRender(header.column.columnDef.header, header.getContext())}
                          <span>
                            {{
                              asc: ' 🔼',
                              desc: ' 🔽',
                            }[header.column.getIsSorted() as string] ?? null}
                          </span>
                        </div>
                      </th>
                    ))}
                  </tr>
                ))}
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {table.getRowModel().rows.length > 0 ? (
                  table.getRowModel().rows.map(row => (
                    <tr key={row.id} className="hover:bg-gray-50">
                      {row.getVisibleCells().map(cell => (
                        <td
                          key={cell.id}
                          className="px-6 py-4 whitespace-nowrap text-sm text-gray-500"
                        >
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                        </td>
                      ))}
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      colSpan={columns.length}
                      className="px-6 py-4 text-center text-sm text-gray-500"
                    >
                      No repair services found
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div className="flex items-center justify-between px-4 py-3 bg-white border border-gray-200 rounded-b-md sm:px-6 h-16">
        <div className="flex items-center">
          <span className="text-sm text-gray-700">
            Page <span className="font-medium">{table.getState().pagination.pageIndex + 1}</span> of{' '}
            <span className="font-medium">{pageInfo?.totalPages || 1}</span>
            {pageInfo && ` (${pageInfo.totalItems} total items)`}
          </span>
        </div>
        <div className="flex space-x-2">
          <PaginationButton
            onClick={() => {
              const newPage = Math.max(0, pagination.pageIndex - 1);
              onPageChange(newPage, pagination.pageSize);
            }}
            disabled={pagination.pageIndex <= 0}
          >
            Previous
          </PaginationButton>
          <PaginationButton
            onClick={() => {
              const newPage = pagination.pageIndex + 1;
              onPageChange(newPage, pagination.pageSize);
            }}
            disabled={pageInfo ? pagination.pageIndex >= pageInfo.totalPages - 1 : true}
          >
            Next
          </PaginationButton>
        </div>
      </div>

      <Modal
        isOpen={isEditModalOpen}
        onClose={handleCloseEditModal}
        title="Edit Service Request"
        footer={
          updateError ? (
            <div className="w-full p-2 bg-red-100 border border-red-400 text-red-700 rounded">
              {updateError.message}
            </div>
          ) : null
        }
      >
        {serviceToEdit && (
          <NewServiceRequestForm
            onSave={handleUpdateService}
            onCancel={handleCloseEditModal}
            initialValues={{
              customerName: serviceToEdit.customerName,
              customerPhone: serviceToEdit.customerPhone,
              vehicleMake: serviceToEdit.vehicleMake,
              vehicleModel: serviceToEdit.vehicleModel,
              vehicleYear: serviceToEdit.vehicleYear,
              licensePlate: serviceToEdit.licensePlate,
              serviceDescription: serviceToEdit.serviceDescription,
              odometerReading: serviceToEdit.odometerReading,
              status: serviceToEdit.status,
            }}
          />
        )}
      </Modal>

      <ConfirmationModal
        isOpen={isDeleteModalOpen}
        onClose={handleCloseDeleteModal}
        onConfirm={handleDeleteService}
        title="Delete Service Request"
        message={
          deleteError ? (
            <div className="p-2 mb-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {deleteError.message}
              <hr className="my-2" />
              Are you sure you want to delete this service request? This action cannot be undone.
            </div>
          ) : (
            'Are you sure you want to delete this service request? This action cannot be undone.'
          )
        }
        confirmButtonText="Delete"
        isLoading={isDeleting}
      />
    </div>
  );
};
