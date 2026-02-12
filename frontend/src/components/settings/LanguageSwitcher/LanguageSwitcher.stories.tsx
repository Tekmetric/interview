/**
 * Storybook Stories for LanguageSwitcher Component
 */

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import LanguageSwitcher from './LanguageSwitcher';
import '../i18n';

const meta: Meta<typeof LanguageSwitcher> = {
  title: 'Components/LanguageSwitcher',
  component: LanguageSwitcher,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <div className="p-4 bg-gradient-to-br from-blue-500 to-purple-600">
        <Story />
      </div>
    ),
  ],
  parameters: {
    docs: {
      description: {
        component:
          'Language selector dropdown supporting 5 languages: English, Spanish, Japanese, French, and German. Uses react-i18next for internationalization.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof LanguageSwitcher>;

/**
 * Default language switcher
 */
export const Default: Story = {};

/**
 * With light background
 */
export const LightBackground: Story = {
  decorators: [
    (Story) => (
      <div className="p-4 bg-white">
        <Story />
      </div>
    ),
  ],
  parameters: {
    docs: {
      description: {
        story: 'Language switcher on a light background (note: may have contrast issues).',
      },
    },
  },
};

/**
 * With dark background
 */
export const DarkBackground: Story = {
  decorators: [
    (Story) => (
      <div className="p-4 bg-gray-900">
        <Story />
      </div>
    ),
  ],
  parameters: {
    docs: {
      description: {
        story: 'Language switcher on a dark background.',
      },
    },
  },
};

/**
 * Interactive example
 */
export const Interactive: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Try switching languages! The selector supports English (en), Spanish (es), Japanese (ja), French (fr), and German (de).',
      },
    },
  },
};
