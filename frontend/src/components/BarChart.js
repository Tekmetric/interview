import React from 'react';
import PropTypes from 'prop-types';
import { Chart, BarSeries, ArgumentAxis, ValueAxis } from '@devexpress/dx-react-chart-material-ui';
import { ValueScale } from '@devexpress/dx-react-chart';

const BarChart = ({ stats }) => {
  if (!stats) return 'Unknown';

  return (
    <div style={{ display: 'flex', gap: '1px', alignItems: 'flex-end', height: '60px', paddingBottom: '4px' }}>
      {stats.map((stat, idx) => {
        const label = stat.stat.name === 'special-attack'
          ? 's. atk'
          : stat.stat.name === 'special-defense'
          ? 's. def'
          : stat.stat.name === 'speed'
          ? 'spd'
          : stat.stat.name.replace('-', ' ').substring(0, 3);
        const chartData = [
          { stat: label, value: stat.base_stat }
        ];
        return (
          <div key={idx} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', minWidth: '30px' }}>
            <div style={{ fontSize: '9px', marginBottom: '2px' }}>{stat.base_stat}</div>
            <div style={{ height: '50px', display: 'flex', justifyContent: 'center' }}>
              <Chart data={chartData} height={80} width={30}>
                <ValueScale modifyDomain={() => [0, 150]} />
                <ArgumentAxis showGrid={false} showLabels={true} />
                <ValueAxis showGrid={false} showLabels={false} />
                <BarSeries valueField="value" argumentField="stat" color="#3b82f6" />
              </Chart>
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
