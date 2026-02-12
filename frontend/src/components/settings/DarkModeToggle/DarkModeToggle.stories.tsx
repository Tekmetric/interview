/**
 * Storybook Stories for DarkModeToggle Component
 */

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import DarkModeToggle from './DarkModeToggle';
import themeReducer from '../../../store/themeSlice';
import '../../../i18n';

// Create a mock store
const createMockStore = (isDarkMode = false) =>
  configureStore({
    reducer: {
      theme: themeReducer,
    },
    preloadedState: {
      theme: { isDarkMode },
    },
  });

const meta: Meta<typeof DarkModeToggle> = {
  title: 'Components/DarkModeToggle',
  component: DarkModeToggle,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <Provider store={createMockStore()}>
        <div className="p-4 bg-gradient-to-br from-blue-500 to-purple-600">
          <Story />
        </div>
      </Provider>
    ),
  ],
  parameters: {
    docs: {
      description: {
        component:
          'Toggle button for switching between light and dark modes. Integrates with Redux theme state.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof DarkModeToggle>;

/**
 * Default light mode state
 */
export const LightMode: Story = {
  decorators: [
    (Story) => (
      <Provider store={createMockStore(false)}>
        <div className="p-4 bg-gradient-to-br from-blue-500 to-purple-600">
          <Story />
        </div>
      </Provider>
    ),
  ],
};

/**
 * Dark mode state
 */
export const DarkMode: Story = {
  decorators: [
    (Story) => (
      <Provider store={createMockStore(true)}>
        <div className="p-4 bg-gradient-to-br from-blue-500 to-purple-600">
          <Story />
        </div>
      </Provider>
    ),
  ],
};

/**
 * Interactive example showing toggle functionality
 */
export const Interactive: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Click the button to toggle between light and dark modes.',
      },
    },
  },
};
