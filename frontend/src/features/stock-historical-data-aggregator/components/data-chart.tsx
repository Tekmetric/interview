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
import NoDataLabel from "./no-data-label";

type DataChartProps = {
  formattedChartData: { date: string; [key: string]: number | string }[];
  selectedSymbols: string[];
  isLoading: boolean;
};

const DataChart = (props: DataChartProps) => {
  const { formattedChartData, selectedSymbols, isLoading } = props;

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
              <stop
                offset="0%"
                stopColor={theme.palette.primary.main}
                stopOpacity={0.2}
              />
              <stop
                offset="100%"
                stopColor={theme.palette.primary.main}
                stopOpacity={0}
              />
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
        />
        <Legend />
        {selectedSymbols.map((symbol) => (
          <Area
            key={symbol}
            type="monotone"
            dataKey={symbol}
            name={symbol}
            stroke={theme.palette.primary.main}
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
