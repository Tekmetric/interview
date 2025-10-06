import React from 'react';
import PropTypes from 'prop-types';

const BarChart = ({ stats }) => {
  if (!stats) return 'Unknown';

  const MAX_STAT_VALUE = 150;

  return (
    <div style={{ display: 'flex', gap: '4px', alignItems: 'flex-end', height: '60px' }}>
      {stats.map((stat, idx) => {
        const label = stat.stat.name === 'special-attack'
          ? 's. atk'
          : stat.stat.name === 'special-defense'
          ? 's. def'
          : stat.stat.name === 'speed'
          ? 'spd'
          : stat.stat.name.substring(0, 3);

        const barHeight = Math.max((stat.base_stat / MAX_STAT_VALUE) * 40, 2);

        return (
          <div key={idx} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-end', minWidth: '35px' }}>
            <div style={{ fontSize: '9px', fontWeight: '500', marginBottom: '2px', color: '#374151' }}>
              {stat.base_stat}
            </div>
            <div
              style={{
                width: '18px',
                height: `${barHeight}px`,
                backgroundColor: '#3b82f6',
                borderRadius: '2px 2px 0 0',
                marginBottom: '2px'
              }}
            />
            <div style={{ fontSize: '7px', textAlign: 'center', color: '#6b7280', textTransform: 'uppercase' }}>
              {label}
            </div>
          </div>
        );
      })}
    </div>
  );
};

BarChart.propTypes = {
  stats: PropTypes.arrayOf(
    PropTypes.shape({
      stat: PropTypes.shape({
        name: PropTypes.string.isRequired,
      }).isRequired,
      base_stat: PropTypes.number.isRequired,
    })
  ),
};

export default BarChart;
