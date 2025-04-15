import { render } from '@testing-library/react';
import Skeleton from './Skeleton';

describe('Skeleton', () => {
  it('renders correctly', () => {
    const { container } = render(<Skeleton />);
    expect(container).toMatchSnapshot();
  });

  it('renders the correct number of blocks', () => {
    const { container } = render(<Skeleton count={3} />);
    expect(container.firstChild?.childNodes.length).toBe(3);
  });

  it('renders one block by default', () => {
    const { container } = render(<Skeleton />);
    expect(container.firstChild?.childNodes.length).toBe(1);
  });
});
