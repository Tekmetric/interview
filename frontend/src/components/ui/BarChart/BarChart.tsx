import React from 'react';
import { PokemonStat } from '../../../types/pokemon';

interface BarChartProps {
  stats?: PokemonStat[];
}

const MAX_STAT_VALUE = 150;
const MAX_BAR_HEIGHT = 40; // Maximum bar height in pixels
const MIN_BAR_HEIGHT = 2; // Minimum visible bar height in pixels

const getStatLabel = (statName: string): string => {
  switch (statName) {
    case 'special-attack':
      return 's. atk';
    case 'special-defense':
      return 's. def';
    case 'speed':
      return 'spd';
    default:
      return statName.substring(0, 3);
  }
};

const BarChart: React.FC<BarChartProps> = ({ stats }) => {
  if (!stats) return <>Unknown</>;

  return (
    <div className="flex gap-1 items-end h-[60px]">
      {stats.map((stat, idx) => {
        const label = getStatLabel(stat.stat.name);
        const barHeightPercentage = (stat.base_stat / MAX_STAT_VALUE) * MAX_BAR_HEIGHT;
        const barHeight = Math.max(barHeightPercentage, MIN_BAR_HEIGHT);

        return (
          <div key={idx} className="flex-1 flex flex-col items-center justify-end min-w-[35px]">
            <div className="text-[9px] font-medium mb-0.5 text-gray-700 dark:text-gray-300">
              {stat.base_stat}
            </div>
            <div
              className="w-[18px] bg-blue-600 dark:bg-blue-500 rounded-t-sm mb-0.5"
              style={{ height: `${barHeight}px` }}
            />
            <div className="text-[7px] text-center text-gray-500 dark:text-gray-400 uppercase">
              {label}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default BarChart;
