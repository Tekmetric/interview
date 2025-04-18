import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import store from '../../../store/store';
import Dashboard from './Dashboard';

describe('Dashboard', () => {
  it('renders correctly', () => {
    const { container } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Dashboard>
            <p>rendered</p>
          </Dashboard>
        </MemoryRouter>
      </Provider>
    );
    expect(container).toMatchSnapshot();
  });
});
