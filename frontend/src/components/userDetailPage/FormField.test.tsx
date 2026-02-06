import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { FormField } from './FormField';

describe('FormField', () => {
  const defaultProps = {
    label: 'Test Field',
    value: '',
    onChange: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Basic Rendering', () => {
    it('renders label and input field', () => {
      render(<FormField {...defaultProps} />);

      expect(screen.getByLabelText('Test Field')).toBeInTheDocument();
      expect(screen.getByRole('textbox')).toBeInTheDocument();
    });

    it('shows required asterisk when required prop is true', () => {
      render(<FormField {...defaultProps} required />);

      expect(screen.getByText('*')).toBeInTheDocument();
    });

    it('applies placeholder text', () => {
      render(<FormField {...defaultProps} placeholder='Enter text here' />);

      expect(screen.getByPlaceholderText('Enter text here')).toBeInTheDocument();
    });

    it('shows initial value', () => {
      render(<FormField {...defaultProps} value='Initial value' />);

      expect(screen.getByDisplayValue('Initial value')).toBeInTheDocument();
    });
  });

  describe('Input Types', () => {
    it('renders text input by default', () => {
      render(<FormField {...defaultProps} />);

      const input = screen.getByRole('textbox');
      expect(input).toHaveAttribute('type', 'text');
    });

    it('renders email input when type is email', () => {
      render(<FormField {...defaultProps} type='email' />);

      const input = screen.getByRole('textbox');
      expect(input).toHaveAttribute('type', 'email');
    });

    it('renders tel input when type is tel', () => {
      render(<FormField {...defaultProps} type='tel' />);

      const input = screen.getByRole('textbox');
      expect(input).toHaveAttribute('type', 'tel');
    });

    it('renders select when type is select and options provided', () => {
      const options = [
        { value: 'option1', label: 'Option 1' },
        { value: 'option2', label: 'Option 2' },
      ];

      render(<FormField {...defaultProps} type='select' options={options} />);

      expect(screen.getByRole('combobox')).toBeInTheDocument();
      expect(screen.getByText('Option 1')).toBeInTheDocument();
      expect(screen.getByText('Option 2')).toBeInTheDocument();
    });
  });

  describe('User Interactions', () => {
    it('calls onChange when user types in input', async () => {
      const onChange = vi.fn();

      render(<FormField {...defaultProps} onChange={onChange} />);

      const input = screen.getByRole('textbox');
      fireEvent.change(input, { target: { value: 'Hello' } });

      expect(onChange).toHaveBeenCalledWith('Hello');
    });

    it('calls onBlur when input loses focus', async () => {
      const onBlur = vi.fn();

      render(<FormField {...defaultProps} onBlur={onBlur} />);

      const input = screen.getByRole('textbox');
      fireEvent.focus(input);
      fireEvent.blur(input);

      expect(onBlur).toHaveBeenCalledTimes(1);
    });

    it('calls onChange when select option is chosen', async () => {
      const onChange = vi.fn();
      const options = [
        { value: 'option1', label: 'Option 1' },
        { value: 'option2', label: 'Option 2' },
      ];

      render(<FormField {...defaultProps} type='select' options={options} onChange={onChange} />);

      const select = screen.getByRole('combobox');
      fireEvent.change(select, { target: { value: 'option2' } });

      expect(onChange).toHaveBeenCalledWith('option2');
    });
  });

  describe('Error States', () => {
    it('displays error message when error prop is provided', () => {
      render(<FormField {...defaultProps} error='This field is required' />);

      expect(screen.getByText('This field is required')).toBeInTheDocument();
    });

    it('applies error styling when error exists', () => {
      render(<FormField {...defaultProps} error='Error message' />);

      const input = screen.getByRole('textbox');
      expect(input).toHaveClass('border-red-500');
    });

    it('does not display error when error prop is not provided', () => {
      render(<FormField {...defaultProps} />);

      expect(screen.queryByText(/error/i)).not.toBeInTheDocument();
    });
  });

  describe('Disabled State', () => {
    it('disables input when disabled prop is true', () => {
      render(<FormField {...defaultProps} disabled />);

      const input = screen.getByRole('textbox');
      expect(input).toBeDisabled();
    });

    it('applies disabled styling', () => {
      render(<FormField {...defaultProps} disabled />);

      const input = screen.getByRole('textbox');
      expect(input).toHaveClass('bg-gray-100');
      expect(input).toHaveClass('cursor-not-allowed');
    });

    it('does not call onChange when disabled', async () => {
      const onChange = vi.fn();

      render(<FormField {...defaultProps} disabled onChange={onChange} />);

      const input = screen.getByRole('textbox');
      fireEvent.change(input, { target: { value: 'Hello' } });

      expect(onChange).not.toHaveBeenCalled();
    });
  });

  describe('Accessibility', () => {
    it('associates label with input using aria-describedby for errors', () => {
      render(<FormField {...defaultProps} error='Error message' />);

      const input = screen.getByRole('textbox');
      expect(input).toHaveAttribute('aria-describedby');
    });

    it('marks required fields appropriately', () => {
      render(<FormField {...defaultProps} required />);

      const input = screen.getByRole('textbox');
      expect(input).toBeRequired();
    });
  });

  describe('Custom Classes', () => {
    it('applies custom className to container', () => {
      render(<FormField {...defaultProps} className='custom-class' />);

      const container = screen.getByText('Test Field').closest('div');
      expect(container).toHaveClass('custom-class');
    });
  });
});
