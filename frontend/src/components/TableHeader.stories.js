import React from 'react';
import TableHeader from './TableHeader';

export default {
  title: 'Components/TableHeader',
  component: TableHeader,
  parameters: {
    layout: 'fullwidth',
  },
  tags: ['autodocs'],
  argTypes: {
    isMobile: {
      control: 'boolean',
      description: 'Whether to render in mobile mode',
    },
  },
};

export const Desktop = {
  args: {
    isMobile: false,
  },
  render: (args) => (
    <div className="bg-white dark:bg-slate-800 p-4">
      <TableHeader {...args} />
    </div>
  ),
};

export const Mobile = {
  args: {
    isMobile: true,
  },
  render: (args) => (
    <div className="bg-white dark:bg-slate-800 p-4">
      <TableHeader {...args} />
    </div>
  ),
};

export const DarkMode = {
  args: {
    isMobile: false,
  },
  render: (args) => {
    React.useEffect(() => {
      document.documentElement.classList.add('dark');
      return () => document.documentElement.classList.remove('dark');
    }, []);

    return (
      <div className="bg-slate-800 p-4">
        <TableHeader {...args} />
      </div>
    );
  },
};
