import { useMemo } from "react";
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { format } from "date-fns";
import { DATE_FORMAT } from "@/lib/constants/global";
import theme from "@/theme";
import NoDataLabel from "@/components/data-chart/components/no-data-label";
import getRandomColor from "@/lib/utils/get-random-color";

type DataChartProps = {
  formattedChartData: { date: string; [key: string]: number | string }[];
  selectedSymbols: string[];
  isLoading: boolean;
};

const DataChart = (props: DataChartProps) => {
  const { formattedChartData, selectedSymbols, isLoading } = props;

  const chartLineColors = useMemo(() => {
    const colorMap: { [key: string]: string } = {};
    selectedSymbols.forEach((symbol) => {
      colorMap[symbol] = getRandomColor();
    });
    return colorMap;
  }, [selectedSymbols]);

  if ((!isLoading && !formattedChartData) || formattedChartData.length === 0) {
    return <NoDataLabel />;
  }

  return (
    <ResponsiveContainer>
      <AreaChart data={formattedChartData}>
        <defs>
          {selectedSymbols.map((symbol) => (
            <linearGradient
              id={`color${symbol}`}
              key={symbol}
              x1="0"
              y1="0"
              x2="0"
              y2="1"
            >
              <stop offset="0%" stopColor={chartLineColors[symbol]} stopOpacity={0.2} />
              <stop offset="100%" stopColor={chartLineColors[symbol]} stopOpacity={0} />
            </linearGradient>
          ))}
        </defs>
        <XAxis dataKey="date" hide />
        <YAxis orientation="right" width={40} strokeWidth={0.5} />
        <Tooltip
          labelFormatter={(label) => format(new Date(label), DATE_FORMAT)}
          contentStyle={{
            backgroundColor: theme.palette.background.paper,
            borderColor: theme.palette.divider,
          }}
          itemStyle={{ color: theme.palette.text.primary }}
          formatter={(value, name) => (
            <span>
              <span
                style={{
                  display: "inline-block",
                  width: "10px",
                  height: "10px",
                  backgroundColor: chartLineColors[name as string],
                  marginRight: "5px",
                }}
              ></span>
              {value}
            </span>
          )}
        />
        <Legend />
        {selectedSymbols.map((symbol) => (
          <Area
            key={symbol}
            type="monotone"
            dataKey={symbol}
            name={symbol}
            stroke={chartLineColors[symbol]}
            fillOpacity={1}
            fill={`url(#color${symbol})`}
            dot={false}
          />
        ))}
      </AreaChart>
    </ResponsiveContainer>
  );
};

export default DataChart;
