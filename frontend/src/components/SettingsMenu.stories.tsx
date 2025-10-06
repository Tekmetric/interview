/**
 * Storybook Stories for SettingsMenu Component
 */

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import SettingsMenu from './SettingsMenu';
import themeReducer from '../store/themeSlice';
import '../i18n';

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

const meta: Meta<typeof SettingsMenu> = {
  title: 'Components/SettingsMenu',
  component: SettingsMenu,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <Provider store={createMockStore()}>
        <div className="p-8 bg-gradient-to-br from-blue-500 to-purple-600 min-h-screen flex justify-end">
          <Story />
        </div>
      </Provider>
    ),
  ],
  parameters: {
    docs: {
      description: {
        component:
          'Hovering settings menu containing dark mode toggle and language switcher. Provides a cleaner UI by grouping settings controls.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof SettingsMenu>;

/**
 * Default settings menu
 */
export const Default: Story = {};

/**
 * Menu opened (simulated)
 */
export const Opened: Story = {
  parameters: {
    docs: {
      description: {
        story: 'Click the settings button to open the dropdown menu.',
      },
    },
  },
};

/**
 * With dark mode enabled
 */
export const DarkMode: Story = {
  decorators: [
    (Story) => (
      <Provider store={createMockStore(true)}>
        <div className="p-8 bg-gradient-to-br from-blue-500 to-purple-600 min-h-screen flex justify-end">
          <Story />
        </div>
      </Provider>
    ),
  ],
  parameters: {
    docs: {
      description: {
        story: 'Settings menu with dark mode enabled.',
      },
    },
  },
};

/**
 * Mobile view
 */
export const Mobile: Story = {
  parameters: {
    viewport: {
      defaultViewport: 'mobile1',
    },
    docs: {
      description: {
        story: 'Settings menu on mobile - "Settings" text is hidden, only icon shown.',
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
          'Try clicking the settings button, toggling dark mode, and switching languages!',
      },
    },
  },
};
