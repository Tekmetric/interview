import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import VehicleFormHeader from './VehicleFormHeader';

describe('VehicleFormHeader', () => {
  const mockOnDeleteImage = vi.fn();
  const mockOnEditClick = vi.fn();

  it('renders correctly', () => {
    const { container } = render(
      <MemoryRouter>
        <VehicleFormHeader
          imageUrl={null}
          isDisabled={true}
          isEditMode={false}
          onDeleteImage={mockOnDeleteImage}
          onEditClick={mockOnEditClick}
          vehicleTitle={'Honda CRV, 2018'}
        />
      </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
  });
});
