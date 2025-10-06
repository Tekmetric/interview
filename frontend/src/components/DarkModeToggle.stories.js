import React from 'react';
import DarkModeToggle from './DarkModeToggle';

export default {
  title: 'Components/DarkModeToggle',
  component: DarkModeToggle,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => <DarkModeToggle />,
};

export const InHeader = {
  render: () => (
    <div className="bg-gradient-to-br from-[#FF6B6B] to-[#C92A2A] p-6 rounded-lg">
      <DarkModeToggle />
    </div>
  ),
};

export const DarkMode = {
  render: () => {
    // Toggle dark mode for this story
    React.useEffect(() => {
      document.documentElement.classList.add('dark');
      return () => document.documentElement.classList.remove('dark');
    }, []);

    return (
      <div className="bg-slate-800 p-6 rounded-lg">
        <DarkModeToggle />
      </div>
    );
  },
};
