import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import UnderConstruction from './UnderConstruction';

describe('UnderConstruction', () => {
  it('renders correctly', () => {
    const { container } = render(
      <MemoryRouter>
        <UnderConstruction />
      </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
  });
});
