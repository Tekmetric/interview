import React from 'react';
import '../src/index.css';
import '../src/i18n';
import { ThemeProvider } from '../src/contexts/ThemeContext';

/** @type { import('@storybook/react').Preview } */
const preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
  decorators: [
    (Story) => (
      <ThemeProvider>
        <div style={{ padding: '20px' }}>
          <Story />
        </div>
      </ThemeProvider>
    ),
  ],
};

export default preview;
