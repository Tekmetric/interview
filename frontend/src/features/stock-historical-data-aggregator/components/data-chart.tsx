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

type DataChartProps = {
  formattedChartData: { date: string; [key: string]: number | string }[];
  selectedSymbols: string[];
};

const DataChart = (props: DataChartProps) => {
  const { formattedChartData, selectedSymbols } = props;

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
              <stop offset="0%" stopColor="#8884d8" stopOpacity={0.2} />
              <stop offset="100%" stopColor="#8884d8" stopOpacity={0} />
            </linearGradient>
          ))}
        </defs>
        <XAxis dataKey="date" hide />
        <YAxis />
        <Tooltip
          labelFormatter={(label) => format(new Date(label), DATE_FORMAT)}
          contentStyle={{
            backgroundColor: theme.palette.background.paper,
            borderColor: theme.palette.divider,
          }}
          itemStyle={{ color: theme.palette.text.primary }}
        />
        <Legend />
        {selectedSymbols.map((symbol) => (
          <Area
            key={symbol}
            type="monotone"
            dataKey={symbol}
            name={symbol}
            stroke="#8884d8"
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
