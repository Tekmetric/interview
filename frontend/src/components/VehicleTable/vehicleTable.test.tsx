import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { mockVehicles } from '../../__mocks__/vehiclesMock';
import VehicleTable from './VehicleTable';

describe('VehicleTable', () => {
  const mockHandleDeleteVehicle = vi.fn();
  const handleSetSelectedVehicle = vi.fn();

  it('renders correctly', () => {
    const { container } = render(
      <MemoryRouter>
        <VehicleTable
          handleDeleteVehicle={mockHandleDeleteVehicle}
          handleSetSelectedVehicle={handleSetSelectedVehicle}
          vehicles={mockVehicles}
        />
      </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
  });
});
