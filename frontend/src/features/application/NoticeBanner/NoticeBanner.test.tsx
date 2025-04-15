import { render } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import store from '../../../store/store';
import NoticeBanner from './NoticeBanner';

describe('NoticeBanner', () => {
  it('renders correctly', () => {
    const { container } = render(
      <Provider store={store}>
        <MemoryRouter>
          <NoticeBanner />
        </MemoryRouter>
      </Provider>
    );
    expect(container).toMatchSnapshot();
  });
});
