import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import store from '../../../store/store';
import VehicleForm from './VehicleForm';

describe('VehicleForm', () => {
  it('renders correctly', () => {
    const { container } = render(
      <Provider store={store}>
        <MemoryRouter>
          <VehicleForm />
        </MemoryRouter>
      </Provider>
    );
    expect(container).toMatchSnapshot();
  });
});
