import { useState } from 'react';
import { Modal } from './Modal';
import { NewServiceRequestForm } from './NewServiceRequestForm';
import { useCreateRepairService } from '../hooks/useCreateRepairService';
import { RepairService } from '../types/api';

type AddNewServiceButtonProps = {
  onServiceAdded: () => void;
};

export const AddNewServiceButton = ({ onServiceAdded }: AddNewServiceButtonProps) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { createService, error } = useCreateRepairService();

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleSave = async (formData: any) => {
    const serviceData: Omit<RepairService, 'id'> = {
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

    await createService(serviceData);
    setIsModalOpen(false);
    onServiceAdded();
  };

  return (
    <>
      <button
        onClick={handleOpenModal}
        className="px-4 py-2 bg-blue-600 text-white rounded-md shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        Add New Service
      </button>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title="New Service Request"
        footer={
          <div className="w-full">
            {error && (
              <div className="mb-3 p-2 bg-red-100 border border-red-400 text-red-700 rounded">
                {error.message}
              </div>
            )}
          </div>
        }
      >
        <NewServiceRequestForm onSave={handleSave} onCancel={handleCloseModal} />
      </Modal>
    </>
  );
};
