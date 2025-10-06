import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import BarChart from './BarChart';

describe('BarChart', () => {
  test('renders "Unknown" when stats is undefined', () => {
    const { container } = render(<BarChart stats={undefined} />);
    expect(container.textContent).toBe('Unknown');
  });

  test('renders "Unknown" when stats is null', () => {
    const { container } = render(<BarChart stats={null} />);
    expect(container.textContent).toBe('Unknown');
  });

  test('renders bar chart with stats data', () => {
    const mockStats = [
      { stat: { name: 'hp' }, base_stat: 45 },
      { stat: { name: 'attack' }, base_stat: 49 },
      { stat: { name: 'defense' }, base_stat: 50 },
    ];

    const { getByText } = render(<BarChart stats={mockStats} />);

    // Check that stat values are displayed
    expect(getByText('45')).toBeInTheDocument();
    expect(getByText('49')).toBeInTheDocument();
    expect(getByText('50')).toBeInTheDocument();
  });

  test('renders special-attack stat with abbreviated label', () => {
    const mockStats = [
      { stat: { name: 'special-attack' }, base_stat: 65 },
    ];

    const { getByText } = render(<BarChart stats={mockStats} />);
    expect(getByText('65')).toBeInTheDocument();
    expect(getByText('s. atk')).toBeInTheDocument();
  });

  test('renders special-defense stat with abbreviated label', () => {
    const mockStats = [
      { stat: { name: 'special-defense' }, base_stat: 65 },
    ];

    const { getByText } = render(<BarChart stats={mockStats} />);
    expect(getByText('65')).toBeInTheDocument();
    expect(getByText('s. def')).toBeInTheDocument();
  });

  test('renders speed stat with abbreviated label', () => {
    const mockStats = [
      { stat: { name: 'speed' }, base_stat: 45 },
    ];

    const { getByText } = render(<BarChart stats={mockStats} />);
    expect(getByText('45')).toBeInTheDocument();
    expect(getByText('spd')).toBeInTheDocument();
  });

  test('renders regular stat names correctly', () => {
    const mockStats = [
      { stat: { name: 'hp' }, base_stat: 45 },
      { stat: { name: 'attack' }, base_stat: 49 },
    ];

    const { getByText } = render(<BarChart stats={mockStats} />);
    expect(getByText('45')).toBeInTheDocument();
    expect(getByText('49')).toBeInTheDocument();
    expect(getByText('hp')).toBeInTheDocument();
    expect(getByText('att')).toBeInTheDocument();
  });

  test('renders all stat types together', () => {
    const mockStats = [
      { stat: { name: 'hp' }, base_stat: 45 },
      { stat: { name: 'attack' }, base_stat: 49 },
      { stat: { name: 'defense' }, base_stat: 49 },
      { stat: { name: 'special-attack' }, base_stat: 65 },
      { stat: { name: 'special-defense' }, base_stat: 65 },
      { stat: { name: 'speed' }, base_stat: 45 },
    ];

    const { getAllByText } = render(<BarChart stats={mockStats} />);

    // All stat values should be present
    expect(getAllByText('45').length).toBeGreaterThan(0);
    expect(getAllByText('49').length).toBeGreaterThan(0);
    expect(getAllByText('65').length).toBeGreaterThan(0);
  });
});
