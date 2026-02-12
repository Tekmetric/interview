import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import { KeyboardNavigationWrapper, useKeyboardNavigation } from './KeyboardNavigation';

describe('KeyboardNavigation', () => {
  let mockListRef;

  beforeEach(() => {
    mockListRef = {
      current: {
        state: {
          scrollOffset: 100,
        },
        scrollTo: jest.fn(),
        scrollToItem: jest.fn(),
      },
    };
  });

  describe('useKeyboardNavigation', () => {
    test('ignores keyboard events on input elements', () => {
      const TestComponent = () => {
        const handleKeyDown = useKeyboardNavigation(mockListRef, 100);
        return (
          <div onKeyDown={handleKeyDown}>
            <input data-testid="test-input" />
          </div>
        );
      };

      const { getByTestId } = render(<TestComponent />);
      const input = getByTestId('test-input');

      fireEvent.keyDown(input, { key: 'ArrowDown' });
      expect(mockListRef.current.scrollTo).not.toHaveBeenCalled();
    });

    test('ignores keyboard events on anchor elements', () => {
      const TestComponent = () => {
        const handleKeyDown = useKeyboardNavigation(mockListRef, 100);
        return (
          <div onKeyDown={handleKeyDown}>
            <a href="#test" data-testid="test-link">Link</a>
          </div>
        );
      };

      const { getByTestId } = render(<TestComponent />);
      const link = getByTestId('test-link');

      fireEvent.keyDown(link, { key: 'ArrowDown' });
      expect(mockListRef.current.scrollTo).not.toHaveBeenCalled();
    });

    test('ignores keyboard events on button elements', () => {
      const TestComponent = () => {
        const handleKeyDown = useKeyboardNavigation(mockListRef, 100);
        return (
          <div onKeyDown={handleKeyDown}>
            <button data-testid="test-button">Button</button>
          </div>
        );
      };

      const { getByTestId } = render(<TestComponent />);
      const button = getByTestId('test-button');

      fireEvent.keyDown(button, { key: 'ArrowDown' });
      expect(mockListRef.current.scrollTo).not.toHaveBeenCalled();
    });
  });

  describe('KeyboardNavigationWrapper', () => {
    test('renders children correctly', () => {
      const { getByText } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      expect(getByText('Test content')).toBeInTheDocument();
    });

    test('applies className prop', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          className="custom-class"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      expect(container.firstChild).toHaveClass('custom-class');
    });

    test('sets aria-label prop', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      expect(container.firstChild).toHaveAttribute('aria-label', 'Test navigation');
    });

    test('handles ArrowDown key', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'ArrowDown' });
      expect(mockListRef.current.scrollTo).toHaveBeenCalledWith(200);
    });

    test('handles ArrowUp key', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'ArrowUp' });
      expect(mockListRef.current.scrollTo).toHaveBeenCalledWith(0);
    });

    test('prevents ArrowUp from scrolling below 0', () => {
      mockListRef.current.state.scrollOffset = 50;

      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'ArrowUp' });
      expect(mockListRef.current.scrollTo).toHaveBeenCalledWith(0);
    });

    test('handles PageDown key', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'PageDown' });
      expect(mockListRef.current.scrollTo).toHaveBeenCalledWith(500);
    });

    test('handles PageUp key', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'PageUp' });
      expect(mockListRef.current.scrollTo).toHaveBeenCalledWith(0);
    });

    test('prevents PageUp from scrolling below 0', () => {
      mockListRef.current.state.scrollOffset = 200;

      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'PageUp' });
      expect(mockListRef.current.scrollTo).toHaveBeenCalledWith(0);
    });

    test('handles Home key', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'Home' });
      expect(mockListRef.current.scrollToItem).toHaveBeenCalledWith(0, 'start');
    });

    test('handles End key', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'End' });
      expect(mockListRef.current.scrollToItem).toHaveBeenCalledWith(99, 'end');
    });

    test('does nothing when listRef is null', () => {
      const nullListRef = { current: null };

      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={nullListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      // Should not throw error
      fireEvent.keyDown(container.firstChild, { key: 'ArrowDown' });
    });

    test('ignores unhandled keys', () => {
      const { container } = render(
        <KeyboardNavigationWrapper
          listRef={mockListRef}
          itemCount={100}
          ariaLabel="Test navigation"
        >
          <div>Test content</div>
        </KeyboardNavigationWrapper>
      );

      fireEvent.keyDown(container.firstChild, { key: 'Enter' });
      expect(mockListRef.current.scrollTo).not.toHaveBeenCalled();
      expect(mockListRef.current.scrollToItem).not.toHaveBeenCalled();
    });
  });
});
