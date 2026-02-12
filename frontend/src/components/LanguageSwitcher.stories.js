import React from 'react';
import LanguageSwitcher from './LanguageSwitcher';
import DarkModeToggle from './DarkModeToggle';

export default {
  title: 'Components/LanguageSwitcher',
  component: LanguageSwitcher,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => <LanguageSwitcher />,
};

export const InHeader = {
  render: () => (
    <div className="bg-gradient-to-br from-[#FF6B6B] to-[#C92A2A] p-6 rounded-lg">
      <LanguageSwitcher />
    </div>
  ),
};

export const WithDarkModeToggle = {
  render: () => (
    <div className="bg-gradient-to-br from-[#FF6B6B] to-[#C92A2A] p-6 rounded-lg flex gap-2">
      <DarkModeToggle />
      <LanguageSwitcher />
    </div>
  ),
};
