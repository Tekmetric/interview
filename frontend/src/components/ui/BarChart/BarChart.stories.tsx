/**
 * Storybook Stories for BarChart Component
 */

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import BarChart from './BarChart';
import type { PokemonStat } from '../../../types/pokemon';

const meta: Meta<typeof BarChart> = {
  title: 'Components/BarChart',
  component: BarChart,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Pure CSS bar chart for displaying Pokemon stats (HP, Attack, Defense, etc.). Optimized for performance and accessibility.',
      },
    },
  },
  argTypes: {
    stats: {
      description: 'Array of Pokemon stats to display',
      control: false,
    },
  },
};

export default meta;
type Story = StoryObj<typeof BarChart>;

/**
 * Typical Pokemon stats (Pikachu)
 */
export const Pikachu: Story = {
  args: {
    stats: [
      { stat: { name: 'hp' }, base_stat: 35 },
      { stat: { name: 'attack' }, base_stat: 55 },
      { stat: { name: 'defense' }, base_stat: 40 },
      { stat: { name: 'special-attack' }, base_stat: 50 },
      { stat: { name: 'special-defense' }, base_stat: 50 },
      { stat: { name: 'speed' }, base_stat: 90 },
    ] as PokemonStat[],
  },
};

/**
 * Low stats Pokemon (Magikarp)
 */
export const LowStats: Story = {
  args: {
    stats: [
      { stat: { name: 'hp' }, base_stat: 20 },
      { stat: { name: 'attack' }, base_stat: 10 },
      { stat: { name: 'defense' }, base_stat: 55 },
      { stat: { name: 'special-attack' }, base_stat: 15 },
      { stat: { name: 'special-defense' }, base_stat: 20 },
      { stat: { name: 'speed' }, base_stat: 80 },
    ] as PokemonStat[],
  },
  parameters: {
    docs: {
      description: {
        story: 'Example of a Pokemon with very low stats (Magikarp).',
      },
    },
  },
};

/**
 * High stats Pokemon (Mewtwo)
 */
export const HighStats: Story = {
  args: {
    stats: [
      { stat: { name: 'hp' }, base_stat: 106 },
      { stat: { name: 'attack' }, base_stat: 110 },
      { stat: { name: 'defense' }, base_stat: 90 },
      { stat: { name: 'special-attack' }, base_stat: 154 },
      { stat: { name: 'special-defense' }, base_stat: 90 },
      { stat: { name: 'speed' }, base_stat: 130 },
    ] as PokemonStat[],
  },
  parameters: {
    docs: {
      description: {
        story: 'Example of a legendary Pokemon with very high stats (Mewtwo).',
      },
    },
  },
};

/**
 * Balanced stats (Bulbasaur)
 */
export const Balanced: Story = {
  args: {
    stats: [
      { stat: { name: 'hp' }, base_stat: 45 },
      { stat: { name: 'attack' }, base_stat: 49 },
      { stat: { name: 'defense' }, base_stat: 49 },
      { stat: { name: 'special-attack' }, base_stat: 65 },
      { stat: { name: 'special-defense' }, base_stat: 65 },
      { stat: { name: 'speed' }, base_stat: 45 },
    ] as PokemonStat[],
  },
  parameters: {
    docs: {
      description: {
        story: 'Starter Pokemon with balanced stats (Bulbasaur).',
      },
    },
  },
};

/**
 * Missing stats (null case)
 */
export const NoStats: Story = {
  args: {
    stats: null as any,
  },
  parameters: {
    docs: {
      description: {
        story: 'Fallback UI when stats are unavailable.',
      },
    },
  },
};

/**
 * Empty stats array
 */
export const EmptyStats: Story = {
  args: {
    stats: [] as PokemonStat[],
  },
  parameters: {
    docs: {
      description: {
        story: 'Edge case with empty stats array.',
      },
    },
  },
};
