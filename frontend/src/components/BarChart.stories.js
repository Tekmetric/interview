import BarChart from './BarChart';

const mockStats = [
  { stat: { name: 'hp' }, base_stat: 45 },
  { stat: { name: 'attack' }, base_stat: 49 },
  { stat: { name: 'defense' }, base_stat: 49 },
  { stat: { name: 'special-attack' }, base_stat: 65 },
  { stat: { name: 'special-defense' }, base_stat: 65 },
  { stat: { name: 'speed' }, base_stat: 45 }
];

const highStats = [
  { stat: { name: 'hp' }, base_stat: 106 },
  { stat: { name: 'attack' }, base_stat: 110 },
  { stat: { name: 'defense' }, base_stat: 90 },
  { stat: { name: 'special-attack' }, base_stat: 154 },
  { stat: { name: 'special-defense' }, base_stat: 90 },
  { stat: { name: 'speed' }, base_stat: 130 }
];

export default {
  title: 'Components/BarChart',
  component: BarChart,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  args: {
    stats: mockStats,
  },
};

export const HighStats = {
  args: {
    stats: highStats,
  },
};

export const NoStats = {
  args: {
    stats: null,
  },
};
